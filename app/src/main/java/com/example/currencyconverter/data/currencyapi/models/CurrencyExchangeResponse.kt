package com.example.currencyconverter.data.currencyapi.models

data class CurrencyExchangeResponse(
    val meta: HashMap<String, String>,
    val data: HashMap<String,CurrencyExchangeModel>
)
