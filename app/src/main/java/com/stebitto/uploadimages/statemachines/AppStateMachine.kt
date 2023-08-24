package com.stebitto.uploadimages.statemachines

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.stebitto.uploadimages.actions.Action
import com.stebitto.uploadimages.actions.PickedImages
import com.stebitto.uploadimages.actions.SelectedCountry
import com.stebitto.uploadimages.actions.UploadImages
import com.stebitto.uploadimages.datamodels.domain.AppImage
import com.stebitto.uploadimages.datamodels.domain.UploadImageStatus
import com.stebitto.uploadimages.sources.countries.CountryRepository
import com.stebitto.uploadimages.sources.images.UploadImagesRepository
import com.stebitto.uploadimages.states.AppState
import com.stebitto.uploadimages.states.CountryState
import com.stebitto.uploadimages.states.UploadImagesState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * StateMachine used as single source of truth for Redux pattern
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class AppStateMachine @Inject constructor(
    private val countryRepository: CountryRepository,
    private val uploadImagesRepository: UploadImagesRepository
) : FlowReduxStateMachine<AppState, Action>(initialState = CountryState.Loading) {

    init {
        spec {
            inState<CountryState.Loading> {
                onEnter { state ->
                    try {
                        val countries = countryRepository.getCountries()
                        state.override { CountryState.CountryList(countries) }
                    } catch (e: Exception) {
                        state.override { CountryState.Error(e.localizedMessage) }
                    }
                }
            }

            inState<CountryState.CountryList> {
                on<SelectedCountry> { _, state ->
                    state.override { UploadImagesState.PickImages }
                }
            }

            inState<UploadImagesState.PickImages> {
                on<PickedImages> { action, state ->
                    val images = action.images.map { contentUri ->
                        AppImage(
                            id = UUID.randomUUID().toString(),
                            contentUri = contentUri,
                            name = contentUri.lastPathSegment ?: ""
                        )
                    }
                    state.override { UploadImagesState.UploadedImages(images) }
                }
            }

            inState<UploadImagesState.UploadedImages> {
                val channel = Channel<AppImage>(capacity = Channel.BUFFERED)
                val mutex = Mutex()

                on<UploadImages> { _, state ->
                    val uploadingImages =
                        state.snapshot.images.map { it.copy(status = UploadImageStatus.IS_LOADING) }
                    uploadingImages.forEach {
                        Timber.d("Emitting ${it.id}")
                        channel.send(it)
                    }
                    state.mutate { copy(images = uploadingImages) }
                }

                collectWhileInState(channel.receiveAsFlow()) { item, state ->
                    Timber.d("Received ${item.id}")
                    val uploadedImage = coroutineScope {
                        async { uploadImage(item) }.await()
                    }
                    mutex.withLock {
                        val imageToReplace = state.snapshot.images.find { it.id == item.id }!!
                        state.mutate {
                            copy(images = images.replaceImageById(imageToReplace, uploadedImage))
                        }
                    }
                }
            }
        }
    }

    // Pure functions similar to reducers
    private suspend fun uploadImage(imageToUpload: AppImage): AppImage =
        try {
            val url = uploadImagesRepository.uploadImage(imageToUpload.contentUri)
            imageToUpload.copy(
                status = UploadImageStatus.UPLOADED,
                url = url
            )
        } catch (e: Exception) {
            imageToUpload.copy(status = UploadImageStatus.ERROR)
        }

    private fun List<AppImage>.replaceImageById(oldImage: AppImage, newItem: AppImage) =
        map { if (it.id == oldImage.id) newItem else it }
}