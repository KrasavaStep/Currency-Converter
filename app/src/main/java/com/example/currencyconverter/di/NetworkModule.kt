package com.example.currencyconverter.di

import com.example.currencyconverter.BuildConfig
import com.example.currencyconverter.data.cryptocurrency_api.CryptocurrencyApi
import com.example.currencyconverter.data.currency_api.CurrencyApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    single<Retrofit>(named("CurrencyApi")){
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CurrencyApi.CURRENCY_API_URL)
            .client(get())
            .build()
    }

    single<Retrofit>(named("CryptoApi")){
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CryptocurrencyApi.CRYPTOCURRENCY_API_URL)
            .client(get())
            .build()
    }

    single<OkHttpClient> {
        val httpClient = OkHttpClient.Builder()
        httpClient.build()

        if (BuildConfig.DEBUG){
            OkHttpClient.Builder().addInterceptor((get<HttpLoggingInterceptor>())).build()
        }
        else{
            OkHttpClient.Builder().build()
        }
    }

    single<CurrencyApi> {
        (get<Retrofit>(named("CurrencyApi"))).create(CurrencyApi::class.java)
    }

    single<CryptocurrencyApi> {
        (get<Retrofit>(named("CryptoApi"))).create(CryptocurrencyApi::class.java)
    }
}

