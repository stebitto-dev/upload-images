package com.stebitto.uploadimages.sources.countries

import com.stebitto.uploadimages.datamodels.api.toDomainModel
import com.stebitto.uploadimages.datamodels.domain.Country
import javax.inject.Inject

interface ICountryRepository {
    suspend fun getCountries() : List<Country>
}

class CountryRepository @Inject constructor(
    private val countryService: CountryService
) : ICountryRepository {

    override suspend fun getCountries() : List<Country> =
        countryService.getCountries().map { it.toDomainModel() }
}