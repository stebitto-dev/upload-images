package com.stebitto.uploadimages.statemachines

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.stebitto.uploadimages.actions.Action
import com.stebitto.uploadimages.sources.countries.CountriesRepository
import com.stebitto.uploadimages.states.CountryState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class AppStateMachine @Inject constructor(
    private val countriesRepository: CountriesRepository
) : FlowReduxStateMachine<CountryState, Action>(initialState = CountryState.Loading) {

    init {
        spec {
            inState<CountryState.Loading> {
                onEnter { state ->
                    try {
                        val countries = countriesRepository.getCountries()
                        state.override { CountryState.CountryList(countries) }
                    } catch (e: Exception) {
                        state.override { CountryState.Error(e.localizedMessage) }
                    }
                }
            }
        }
    }
}