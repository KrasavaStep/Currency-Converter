package com.example.currencyconverter.data.currency_api_widget

import com.example.currencyconverter.BuildConfig
import com.example.currencyconverter.data.currency_api_widget.models.CurrencyWidgetApiResponse
import retrofit2.http.GET

interface CurrencyWidgetAPI {
    @GET("${BuildConfig.WIDGET_KEY}/pair/USD/RUB")
    suspend fun getUSDtoRUB(): CurrencyWidgetApiResponse

    @GET("${BuildConfig.WIDGET_KEY}/pair/USD/EUR")
    suspend fun getUSDtoEUR(): CurrencyWidgetApiResponse

    @GET("${BuildConfig.WIDGET_KEY}/pair/EUR/RUB")
    suspend fun getEURtoRUB(): CurrencyWidgetApiResponse

    companion object {
        const val WIDGET_CUR_API_URL = "https://v6.exchangerate-api.com/v6/"
    }
}