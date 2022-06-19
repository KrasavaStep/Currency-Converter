package com.example.currencyconverter.data.cryptocurrency_api

import com.example.currencyconverter.data.cryptocurrency_api.models.CryptoDataResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptocurrencyApi {

    @GET("coins/markets")
    suspend fun getCryptoData(
        @Query("vs_currency") cur: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("price_change_percentage") percentage: String = "1h"
    ) : List<CryptoDataResponse>

    @GET("coins/{id}/ohlc?vs_currency=usd&days=365")
    suspend fun getCryptoOHLCForYear(@Path("id") id: String) : List<List<Double>>

    @GET("coins/{id}/ohlc?vs_currency=usd&days=1")
    suspend fun getCryptoOHLCForDay(@Path("id") id: String) : List<List<Double>>

    companion object {
        const val CRYPTOCURRENCY_API_URL = "https://api.coingecko.com/api/v3/"
    }
}