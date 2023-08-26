package com.stebitto.uploadimages.di

import com.stebitto.uploadimages.states.AppState
import com.stebitto.uploadimages.states.CountryState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    fun provideAppState(): AppState = CountryState.Loading

    @Provides
    fun provideLoggingHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.NONE }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
}