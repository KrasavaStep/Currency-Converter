package com.example.currencyconverter.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyconverter.data.db.entities.CurrencyItem

@Database(entities = [CurrencyItem::class], version = 1)
abstract class CurrencyDatabase() : RoomDatabase() {
    abstract fun getCurrencyDao(): CurrencyDao
}