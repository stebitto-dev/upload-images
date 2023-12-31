package com.stebitto.uploadimages.di

import android.content.Context
import com.stebitto.uploadimages.sources.images.IUploadImagesRepository
import com.stebitto.uploadimages.sources.images.UploadImagesRepository
import com.stebitto.uploadimages.sources.images.UploadImagesService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object UploadImagesModule {

    @Provides
    fun provideUploadImageRepository(
        @ApplicationContext context: Context,
        uploadImagesService: UploadImagesService
    ): IUploadImagesRepository = UploadImagesRepository(context, uploadImagesService)

    @Provides
    fun provideUploadImagesService(okHttpClient: OkHttpClient): UploadImagesService =
        Retrofit.Builder()
            .baseUrl("https://catbox.moe/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(UploadImagesService::class.java)
}