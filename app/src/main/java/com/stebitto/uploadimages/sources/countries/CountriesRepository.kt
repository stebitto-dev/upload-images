package com.stebitto.uploadimages.sources.countries

import com.stebitto.uploadimages.datamodels.api.toDomainModel
import com.stebitto.uploadimages.datamodels.domain.Country
import javax.inject.Inject

class CountriesRepository @Inject constructor(
    private val countryService: CountryService
) {

    suspend fun getCountries() : List<Country> =
        countryService.getCountries().map { it.toDomainModel() }
}