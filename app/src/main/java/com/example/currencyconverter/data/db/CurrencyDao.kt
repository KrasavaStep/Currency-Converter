package com.example.currencyconverter.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.currencyconverter.data.db.entities.CryptocurrencyItem
import com.example.currencyconverter.data.db.entities.CurrencyItem
import com.example.currencyconverter.data.db.entities.ExchangeItem

@Dao
interface CurrencyDao {

    @Insert(onConflict = REPLACE)
    suspend fun addCurrencyData(item: CurrencyItem)

    @Insert(onConflict = REPLACE)
    suspend fun addExchangeData(item: ExchangeItem)

    @Query("SELECT * FROM currency_table ORDER BY name")
    fun getAllData() : LiveData<List<CurrencyItem>>

    @Query("SELECT * FROM currency_table WHERE isFavourite = 1 ORDER BY name")
    fun getFavouritesCurrencies(): LiveData<List<CurrencyItem>>

    @Query("SELECT * FROM ExchangeItem WHERE code = :code")
    suspend fun getExchangeRateForCurrency(code: String) : ExchangeItem?

    @Query("SELECT * FROM currency_table WHERE code = :code")
    suspend fun getCurrencyByCode(code: String) : CurrencyItem?

    @Query("SELECT * FROM currency_table WHERE name LIKE :name ORDER BY name")
    fun getDataForSearch(name: String) : LiveData<List<CurrencyItem>>

    @Update
    suspend fun updateCurrencyItem(currencyItem: CurrencyItem)

    //Cryptocurrency

    @Insert(onConflict = REPLACE)
    suspend fun addCryptoData(item: CryptocurrencyItem)

    @Query("SELECT * FROM cryptocurrency_data")
    fun getAllCryptos(): LiveData<List<CryptocurrencyItem>>

}