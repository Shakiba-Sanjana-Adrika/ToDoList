package com.example.to_dolist.di

import com.example.to_dolist.data.network.ApiService
import com.example.to_dolist.data.network.ApiUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ApiUrl.BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}