package com.example.currencyconverter.data.cryptocurrency_api

import com.example.currencyconverter.data.cryptocurrency_api.models.CryptoItemModel
import com.example.currencyconverter.data.cryptocurrency_api.models.PriceModel
import com.example.currencyconverter.data.cryptocurrency_api.models.crypto_data_models.CryptoDataResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptocurrencyApi {

    @GET("coins/list?include_platform=false")
    suspend fun getCryptoList(): List<CryptoItemModel>

    @GET("simple/price")
    suspend fun getCryptoPrices(
        @Query("ids") id: String,
        @Query("vs_currencies") cur: String = "usd"): HashMap<String, PriceModel>

    @GET("coins/{id}?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false&sparkline=false")
    suspend fun getCryptoData(@Path("id") id: String) : CryptoDataResponse

    @GET("coins/{id}/ohlc?vs_currency=usd&days=1")
    suspend fun getCryptoOHLCForYear(@Path("id") id: String) : List<List<Float>>

    @GET("coins/{id}/ohlc?vs_currency=usd&days=365")
    suspend fun getCryptoOHLCForDay(@Path("id") id: String) : List<List<Float>>

    companion object {
        const val CRYPTOCURRENCY_API_URL = "https://api.coingecko.com/api/v3/"
    }
}