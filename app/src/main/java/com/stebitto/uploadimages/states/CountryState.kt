package com.stebitto.uploadimages.states

import com.stebitto.uploadimages.datamodels.domain.Country

sealed interface CountryState : AppState {

    object Loading : CountryState

    data class SelectCountry(val selectedCountry: Country) : CountryState

    data class CountryList(val countries: List<Country>) : CountryState

    data class Error(val message: String?) : CountryState
}
