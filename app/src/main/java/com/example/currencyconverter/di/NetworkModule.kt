package com.example.currencyconverter.di

import com.example.currencyconverter.BuildConfig
import com.example.currencyconverter.data.currencyapi.CurrencyApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    single<Retrofit>{
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CurrencyApi.CURRENCY_API_URL)
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
        (get<Retrofit>()).create(CurrencyApi::class.java)
    }

}

