package com.example.currencyconverter.data

import androidx.lifecycle.LiveData
import com.example.currencyconverter.data.cryptocurrency_api.CryptocurrencyApi
import com.example.currencyconverter.data.db.CurrencyDao
import com.example.currencyconverter.data.db.entities.CryptocurrencyItem

class CryptoRepository(
    private val currencyDao: CurrencyDao,
    private val cryptoApi: CryptocurrencyApi,
) {

    val allCryptos: LiveData<List<CryptocurrencyItem>> = currencyDao.getAllCryptos()

    suspend fun getCryptoData() = cryptoApi.getCryptoData()

    suspend fun addCryptoData(item: CryptocurrencyItem) = currencyDao.addCryptoData(item)

    suspend fun getOHLCForDay(id: String) = cryptoApi.getCryptoOHLCForDay(id)
    suspend fun getOHLCForYear(id: String) = cryptoApi.getCryptoOHLCForYear(id)

    fun getCryptoForSearch(name: String) = currencyDao.getCryptoForSearch(name)
}