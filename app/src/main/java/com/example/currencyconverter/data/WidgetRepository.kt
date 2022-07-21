package com.example.currencyconverter.data

import com.example.currencyconverter.data.currency_api_widget.CurrencyWidgetAPI

class WidgetRepository(private val widgetCurApi: CurrencyWidgetAPI) {

    suspend fun getUSDtoRUB() = widgetCurApi.getUSDtoRUB()

    suspend fun getUSDtoEUR() = widgetCurApi.getUSDtoEUR()

    suspend fun getEURtoRUB() = widgetCurApi.getEURtoRUB()
}