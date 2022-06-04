package com.example.currencyconverter.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class CurrencyItem(
    val symbol: String,
    val name: String,
    val symbol_native: String,
    val decimal_digits: Int,
    val rounding: Int,
    @PrimaryKey
    val code: String,
    val name_plural: String,
    val value: Float
)
