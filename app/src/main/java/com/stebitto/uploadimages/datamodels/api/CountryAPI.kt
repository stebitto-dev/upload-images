package com.stebitto.uploadimages.datamodels.api

import com.stebitto.uploadimages.datamodels.domain.Country

data class CountryAPI(
    val iso: Int,
    val isoAlpha2: String,
    val isoAlpha3: String,
    val name: String,
    val phonePrefix: String
)

fun CountryAPI.toDomainModel() = Country(name)