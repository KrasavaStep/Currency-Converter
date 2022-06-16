package com.example.currencyconverter.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cryptocurrency_data")
data class CryptocurrencyItem(
    @PrimaryKey
    val id: String,
    val symbol: String?,
    val name: String?,
    val image: String?,
    val currentPrice: Double,
    val marketCap: Double?,
    val fullyDilutedValuation: Double?,
    val totalVolume: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val priceChange24h: Double?,
    val priceChangePercentage24h: Double,
    val marketCapChange24h: Double?,
    val marketCapChangePercentage24h: Double?,
    val priceChangePercentage1hInCurrency: Double
)