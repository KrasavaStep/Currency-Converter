package com.example.currencyconverter.data.currencyapi.models

data class CurrencyApiModel(
    val symbol: String,
    val name: String,
    val symbol_native: String,
    val decimal_digits: Int,
    val rounding: Int,
    val code: String,
    val name_plural: String
)
