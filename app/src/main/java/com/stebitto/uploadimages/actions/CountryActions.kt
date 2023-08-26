package com.stebitto.uploadimages.actions

import com.stebitto.uploadimages.datamodels.domain.Country

data class SelectedCountry(
    val country: Country
) : Action

object RetryLoadingCountries : Action
