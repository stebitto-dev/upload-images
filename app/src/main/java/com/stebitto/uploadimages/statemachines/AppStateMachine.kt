package com.stebitto.uploadimages.statemachines

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.stebitto.uploadimages.actions.Action
import com.stebitto.uploadimages.actions.PickedImages
import com.stebitto.uploadimages.actions.RemoveImage
import com.stebitto.uploadimages.actions.RetryLoadingCountries
import com.stebitto.uploadimages.actions.SelectedCountry
import com.stebitto.uploadimages.actions.UploadImages
import com.stebitto.uploadimages.datamodels.domain.AppImage
import com.stebitto.uploadimages.datamodels.domain.UploadImageStatus
import com.stebitto.uploadimages.sources.countries.ICountryRepository
import com.stebitto.uploadimages.sources.images.IUploadImagesRepository
import com.stebitto.uploadimages.states.AppState
import com.stebitto.uploadimages.states.CountryState
import com.stebitto.uploadimages.states.UploadImagesState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * StateMachine used as single source of truth for Redux pattern
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class AppStateMachine @Inject constructor(
    private val countryRepository: ICountryRepository,
    private val uploadImagesRepository: IUploadImagesRepository,
    initialState: AppState = CountryState.Loading
) : FlowReduxStateMachine<AppState, Action>(initialState = initialState) {

    init {
        spec {
            inState<CountryState.Loading> {
                onEnter { state ->
                    loadCountriesAndMoveToCountryListOrError(state)
                }
            }

            inState<CountryState.Error> {
                on<RetryLoadingCountries> { _, state ->
                    state.override { CountryState.Loading }
                }
            }

            inState<CountryState.CountryList> {
                on<SelectedCountry> { _, state ->
                    state.override { UploadImagesState.PickImages() }
                }
            }

            inState<UploadImagesState.PickImages> {
                on<PickedImages> { action, state ->
                    addNewImagesToCurrentState(action, state)
                }

                on<RemoveImage> { action, state ->
                    // check uploadedImages for image to remove
                    val removeImage = state.snapshot.uploadedImages.find { it.id == action.image.id }
                    if (removeImage != null) {
                        val newUploadedImages = state.snapshot.uploadedImages.toMutableList().apply {
                            remove(removeImage)
                        }
                        state.mutate { (copy(uploadedImages = newUploadedImages)) }
                    } else {
                        // check imagesToUpload for image to remove
                        val newImagesToUpload = state.snapshot.imagesToUpload.toMutableList().apply {
                            remove(find { it.id == action.image.id }!!)
                        }
                        state.mutate { copy(imagesToUpload = newImagesToUpload) }
                    }
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
                // buffered channel to store uploaded image
                val channel = Channel<AppImage>(capacity = Channel.BUFFERED)

                // channel producer
                onEnterEffect { stateSnapshot ->
                    // parallel uploads with coroutines
                    coroutineScope {
                        stateSnapshot.uploadingImages.forEach { image ->
                            val deferred = async { uploadImage(image) }.await()
                            // as soon as upload completes, send it to the channel
                            channel.send(deferred)
                        }
                    }
                }

                // channel consumer
                collectWhileInState(channel.receiveAsFlow()) { uploadedImage, state ->
                    val imageToReplace = state.snapshot.uploadingImages.find { it.id == uploadedImage.id }!!
                    // remove element from images that needs to be uploaded yet
                    val newUploadingImages = state.snapshot.uploadingImages.toMutableList().apply {
                        remove(imageToReplace)
                    }
                    // and place it in uploaded images list
                    val newUploadedImages = state.snapshot.uploadedImages.toMutableList().apply {
                        add(uploadedImage)
                    }

                    if (newUploadingImages.isEmpty()) { // if there are no more images to upload, change state
                        state.override {
                            UploadImagesState.PickImages(uploadedImages = newUploadedImages, imagesToUpload = emptyList())
                        }
                    } else {
                        state.mutate {
                            copy(uploadedImages = newUploadedImages, uploadingImages = newUploadingImages)
                        }
                    }
                }
            }
        }
    }

    // Pure functions similar to reducers
    private suspend fun loadCountriesAndMoveToCountryListOrError(
        state: State<CountryState.Loading>
    ): ChangedState<AppState> {
        return try {
            val countries = countryRepository.getCountries()
            state.override { CountryState.CountryList(countries) }
        } catch (e: Exception) {
            state.override { CountryState.Error(e.localizedMessage) }
        }
    }
    private fun addNewImagesToCurrentState(
        action: PickedImages,
        state: State<UploadImagesState.PickImages>
    ): ChangedState<AppState> {
        // append new picked images to current list
        val imagesToUpload = state.snapshot.imagesToUpload + action.images
        // update current list without changing state
        return state.mutate { copy(imagesToUpload = imagesToUpload) }
    }

    private suspend fun uploadImage(imageToUpload: AppImage): AppImage {
        return try {
            val url = uploadImagesRepository.uploadImage(imageToUpload.contentUri)
            imageToUpload.copy(
                status = UploadImageStatus.UPLOADED,
                url = url
            )
        } catch (e: Exception) {
            imageToUpload.copy(status = UploadImageStatus.ERROR)
        }
    }
}