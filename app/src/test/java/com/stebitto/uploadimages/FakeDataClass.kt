package com.stebitto.uploadimages

import android.net.Uri
import com.stebitto.uploadimages.datamodels.domain.Country
import com.stebitto.uploadimages.sources.countries.ICountryRepository
import com.stebitto.uploadimages.sources.images.IUploadImagesRepository

class FakeCountriesRepository(
    private val items: List<Country> = emptyList(),
    private val exception: Exception? = null
) : ICountryRepository {
    override suspend fun getCountries(): List<Country> =
        if (exception != null)
            throw exception
        else
            items
}

class FakeUploadImagesRepository(
    private val url: String = ""
) : IUploadImagesRepository {
    override suspend fun uploadImage(contentUri: Uri): String = url
}