package com.example.currencyconverter.data.currencyapi

import com.example.currencyconverter.BuildConfig
import com.example.currencyconverter.data.currencyapi.models.CurrencyApiResponse
import com.example.currencyconverter.data.currencyapi.models.CurrencyExchangeResponse
import retrofit2.Call
import retrofit2.http.GET

interface CurrencyApi {

    @GET("currencies?apikey=${BuildConfig.CURRENCY_KEY}")
    fun getAllCurrency() : Call<CurrencyApiResponse>

    @GET("latest?apikey=${BuildConfig.CURRENCY_KEY}")
    fun getLatestExchangeRates() : Call<CurrencyExchangeResponse>

    companion object{
        const val CURRENCY_API_URL = "https://api.currencyapi.com/v3/"
    }
}