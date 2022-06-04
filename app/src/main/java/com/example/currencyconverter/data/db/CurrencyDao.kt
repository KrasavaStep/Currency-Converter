package com.example.currencyconverter.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.currencyconverter.data.db.entities.CurrencyItem

@Dao
interface CurrencyDao {

    @Insert(onConflict = REPLACE)
    suspend fun addCurrencyData(item: CurrencyItem)

    @Query("SELECT * FROM currency_table")
    fun getAllData() : LiveData<List<CurrencyItem>>

    @Query("SELECT * FROM currency_table WHERE code = :code")
    fun selectCurrency(code: String) : LiveData<CurrencyItem>

}