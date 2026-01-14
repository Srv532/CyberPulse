package com.cyberpulse.core.di

import com.cyberpulse.BuildConfig
import com.cyberpulse.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt Module for Network Dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    // Base URLs for different APIs
    private const val NEWS_BASE_URL = "https://api.newsapi.org/v2/"
    private const val HIBP_BASE_URL = "https://haveibeenpwned.com/api/v3/"
    private const val NVD_BASE_URL = "https://services.nvd.nist.gov/rest/json/"
    private const val CTFTIME_BASE_URL = "https://ctftime.org/api/v1/"
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        // Certificate Pinning
        val certificatePinner = okhttp3.CertificatePinner.Builder()
            .add("api.newsapi.org", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=") // REPLACE THIS with real hash
            .add("haveibeenpwned.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=") // REPLACE THIS with real hash
            .build()

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Add common headers
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "CyberPulse-Android/1.0")
                    .build()
                chain.proceed(request)
            }
            //.certificatePinner(certificatePinner) // Uncomment when you have real hashes to avoid breaking app
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @Named("NewsRetrofit")
    fun provideNewsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NEWS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    @Named("HIBPRetrofit")
    fun provideHIBPRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // HIBP requires special headers
        val hibpClient = okHttpClient.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("hibp-api-key", BuildConfig.HIBP_API_KEY)
                    .addHeader("User-Agent", "CyberPulse-Android")
                    .build()
                chain.proceed(request)
            }
            .build()
        
        return Retrofit.Builder()
            .baseUrl(HIBP_BASE_URL)
            .client(hibpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    @Named("NVDRetrofit")
    fun provideNVDRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NVD_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    @Named("CTFTimeRetrofit")
    fun provideCTFTimeRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CTFTIME_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API Services
    
    @Provides
    @Singleton
    fun provideNewsApiService(
        @Named("NewsRetrofit") retrofit: Retrofit
    ): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideHIBPApiService(
        @Named("HIBPRetrofit") retrofit: Retrofit
    ): HIBPApiService {
        return retrofit.create(HIBPApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCVEApiService(
        @Named("NVDRetrofit") retrofit: Retrofit
    ): CVEApiService {
        return retrofit.create(CVEApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideEventsApiService(
        @Named("CTFTimeRetrofit") retrofit: Retrofit
    ): EventsApiService {
        return retrofit.create(EventsApiService::class.java)
    }
}
