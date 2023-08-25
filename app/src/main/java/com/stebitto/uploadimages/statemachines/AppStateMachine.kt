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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
                    state.override { UploadImagesState.PickImages() }
                }
            }

            inState<UploadImagesState.PickImages> {
                on<PickedImages> { action, state ->
                    // map uris into domain model objects
                    val newImagesList = action.images.map { contentUri ->
                        AppImage(
                            id = UUID.randomUUID().toString(),
                            contentUri = contentUri
                        )
                    }
                    // append new picked images to current list
                    val imagesToUpload = state.snapshot.imagesToUpload + newImagesList
                    // update current list without changing state
                    state.mutate { copy(imagesToUpload = imagesToUpload) }
                }

                on<UploadImages> { _, state ->
                    // update images status
                    val uploadingImages =
                        state.snapshot.imagesToUpload.map { it.copy(status = UploadImageStatus.IS_LOADING) }

                    // change state to Uploading images
                    state.override {
                        UploadImagesState.UploadingImages(
                            uploadedImages = state.snapshot.uploadedImages,
                            uploadingImages = uploadingImages
                        )
                    }
                }
            }

            inState<UploadImagesState.UploadingImages> {
                onEnter { state ->
                    // parallel uploads with coroutines
                    val deferredList = mutableListOf<Deferred<AppImage>>()
                    state.snapshot.uploadingImages.forEach { image ->
                        val deferred = coroutineScope { async { uploadImage(image) } }
                        deferredList.add(deferred)
                    }
                    // get upload results and append them to current list
                    val uploadedImages = state.snapshot.uploadedImages + deferredList.awaitAll()

                    state.override {
                        UploadImagesState.PickImages(
                            uploadedImages = uploadedImages,
                            imagesToUpload = emptyList()
                        )
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
}