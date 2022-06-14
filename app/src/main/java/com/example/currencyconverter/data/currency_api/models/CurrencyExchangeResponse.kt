package com.example.currencyconverter.data.currency_api.models

data class CurrencyExchangeResponse(
    val meta: HashMap<String, String>,
    val data: HashMap<String,CurrencyExchangeModel>
)
