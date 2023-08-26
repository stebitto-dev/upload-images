package com.stebitto.uploadimages.di

import com.stebitto.uploadimages.sources.countries.CountryRepository
import com.stebitto.uploadimages.sources.countries.CountryService
import com.stebitto.uploadimages.sources.countries.ICountryRepository
import com.stebitto.uploadimages.states.AppState
import com.stebitto.uploadimages.states.CountryState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object CountryModule {

    @Provides
    fun provideCountryRepository(countryService: CountryService): ICountryRepository =
        CountryRepository(countryService)

    @Provides
    fun provideCountryService(okHttpClient: OkHttpClient): CountryService =
        Retrofit.Builder()
            .baseUrl("https://api.photoforse.online")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(CountryService::class.java)
}