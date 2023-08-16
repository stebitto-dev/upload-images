package com.stebitto.uploadimages.statemachines

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.stebitto.uploadimages.actions.Action
import com.stebitto.uploadimages.sources.countries.CountryRepository
import com.stebitto.uploadimages.states.CountryState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class AppStateMachine @Inject constructor(
    private val countryRepository: CountryRepository
) : FlowReduxStateMachine<CountryState, Action>(initialState = CountryState.Loading) {

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
        }
    }
}