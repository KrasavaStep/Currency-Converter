package com.example.currencyconverter.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cryptocurrency_data")
data class CryptocurrencyItem(
    @PrimaryKey
    val id: String,
    val symbol: String,
    val name: String,
    val price: Float,
    val homepageLink: String,
    val blockchainLink: String,
    val forum: String,
    val twitter: String,
    val imageLink: String
)