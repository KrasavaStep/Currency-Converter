package com.example.currencyconverter.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.currencyconverter.data.db.entities.CurrencyItem
import com.example.currencyconverter.data.db.entities.ExchangeItem

@Dao
interface CurrencyDao {

    @Insert(onConflict = REPLACE)
    suspend fun addCurrencyData(item: CurrencyItem)

    @Insert(onConflict = REPLACE)
    suspend fun addExchangeData(item: ExchangeItem)

    @Query("SELECT * FROM currency_table")
    fun getAllData() : LiveData<List<CurrencyItem>>

    @Query("SELECT * FROM ExchangeItem WHERE code = :code")
    suspend fun getExchangeRateForCurrency(code: String) : ExchangeItem?

    @Query("SELECT * FROM currency_table WHERE code = :code")
    suspend fun getCurrencyByCode(code: String) : CurrencyItem?

}