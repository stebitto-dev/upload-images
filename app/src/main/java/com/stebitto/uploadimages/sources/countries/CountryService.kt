package com.stebitto.uploadimages.sources.countries

import com.stebitto.uploadimages.datamodels.api.CountryAPI
import retrofit2.http.GET
import retrofit2.http.Headers

interface CountryService {

    @Headers("x-api-key:AIzaSyCccmdkjGe_9Yt-INL2rCJTNgoS4CXsRDc")
    @GET("/geographics/countries/")
    suspend fun getCountries() : List<CountryAPI>
}