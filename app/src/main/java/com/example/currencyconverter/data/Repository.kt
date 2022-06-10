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
import com.example.currencyconverter.data.db.entities.ExchangeItem
import com.example.currencyconverter.mappers.CurrencyMappers
import retrofit2.Callback

class Repository (private val currencyApi: CurrencyApi, private val currencyDao: CurrencyDao) {
    val getAllCurrencyFromDb: LiveData<List<CurrencyItem>> = currencyDao.getAllData()

    fun getAllCurrency(callback: Callback<CurrencyApiResponse>){
        currencyApi.getAllCurrency().enqueue(callback)
    }

    fun getLatestExchangeRates(callback: Callback<CurrencyExchangeResponse>){
        currencyApi.getLatestExchangeRates().enqueue(callback)
    }

    suspend fun setDataToCurrencyTable(currencies: List<CurrencyItem>){
        currencies.forEach {
            currencyDao.addCurrencyData(it)
        }
    }

    suspend fun setDataToExchangeTable(exchange: List<ExchangeItem>){
        exchange.forEach {
            currencyDao.addExchangeData(it)
        }
    }

    suspend fun getExchangeRateForCurrency(code: String) = currencyDao.getExchangeRateForCurrency(code)
    suspend fun getCurrencyByCode(code: String) = currencyDao.getCurrencyByCode(code)

    fun convertToDollar(currencyValue: Float, currencyExRates: Float): Float { //левое
        return currencyValue / currencyExRates
    }

    fun convertDollarToCurrency(dollarValue: Float, currencyExRates: Float): Float { //правое
        return dollarValue * currencyExRates
    }

    fun convertFirstCurrencyToSecond(
        curFirstExRates: Float,
        curSecondExRates: Float,
        curFirstValue: Float
    ): Float {
        val firstToSecondExRates = curSecondExRates / curFirstExRates
        return curFirstValue * firstToSecondExRates
    }

    fun convertSecondCurrencyToFirst(
        curFirstExRates: Float,
        curSecondExRates: Float,
        curSecondValue: Float
    ): Float {
        val secondToFirstExRates = curFirstExRates / curSecondExRates
        return curSecondValue * secondToFirstExRates
    }


}