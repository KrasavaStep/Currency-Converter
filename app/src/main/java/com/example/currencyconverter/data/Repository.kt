package com.example.currencyconverter.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.currencyconverter.data.currencyapi.CurrencyApi
import com.example.currencyconverter.data.currencyapi.models.CurrencyApiModel
import com.example.currencyconverter.data.currencyapi.models.CurrencyApiResponse
import com.example.currencyconverter.data.currencyapi.models.CurrencyExchangeModel
import com.example.currencyconverter.data.currencyapi.models.CurrencyExchangeResponse
import com.example.currencyconverter.data.db.CurrencyDao
import com.example.currencyconverter.data.db.entities.CurrencyItem
import com.example.currencyconverter.mappers.CurrencyMappers
import retrofit2.Callback

class Repository (private val currencyApi: CurrencyApi, private val currencyDao: CurrencyDao) {
    private val currencyApiMappers = CurrencyMappers()

    val getAllCurrencyFromDb: LiveData<List<CurrencyItem>> = currencyDao.getAllData()

    fun getAllCurrency(callback: Callback<CurrencyApiResponse>){
        currencyApi.getAllCurrency().enqueue(callback)
    }

    fun getLatestExchangeRates(callback: Callback<CurrencyExchangeResponse>){
        currencyApi.getLatestExchangeRates().enqueue(callback)
    }

    suspend fun setDataToDb(currencies: List<CurrencyApiModel>, exRates: List<CurrencyExchangeModel>){
        val data = currencyApiMappers.fromCurrencyApiToDbModel(currencies, exRates)

        data.forEach {
            currencyDao.addCurrencyData(it)
            Log.d("MyApp", "$it")
        }
    }

}