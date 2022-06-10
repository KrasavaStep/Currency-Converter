package com.example.currencyconverter.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExchangeItem(
    @PrimaryKey
    val code: String,
    val value: Float
)
