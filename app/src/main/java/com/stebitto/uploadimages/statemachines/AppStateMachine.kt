package com.stebitto.uploadimages.statemachines

import android.net.Uri
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.stebitto.uploadimages.actions.Action
import com.stebitto.uploadimages.actions.SelectedCountry
import com.stebitto.uploadimages.actions.UploadImages
import com.stebitto.uploadimages.sources.countries.CountryRepository
import com.stebitto.uploadimages.sources.images.UploadImagesRepository
import com.stebitto.uploadimages.states.AppState
import com.stebitto.uploadimages.states.CountryState
import com.stebitto.uploadimages.states.UploadImagesState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

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
                on<UploadImages> { action, state ->
                    for (uri in action.images) {
                        uploadImage(uri)
                    }
                    state.override { UploadImagesState.UploadedImages }
                }
            }
        }
    }

    private suspend fun uploadImage(uri: Uri) = coroutineScope {
        launch { uploadImagesRepository.uploadImage(uri) }
    }
}