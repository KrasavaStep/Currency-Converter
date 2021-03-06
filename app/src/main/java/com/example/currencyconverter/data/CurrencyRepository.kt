package com.example.currencyconverter.data

import androidx.lifecycle.LiveData
import com.example.currencyconverter.data.cryptocurrency_api.CryptocurrencyApi
import com.example.currencyconverter.data.currency_api.CurrencyApi
import com.example.currencyconverter.data.currency_api.models.CurrencyApiResponse
import com.example.currencyconverter.data.currency_api.models.CurrencyExchangeResponse
import com.example.currencyconverter.data.currency_api_widget.CurrencyWidgetAPI
import com.example.currencyconverter.data.db.CurrencyDao
import com.example.currencyconverter.data.db.entities.CryptocurrencyItem
import com.example.currencyconverter.data.db.entities.CurrencyItem
import com.example.currencyconverter.data.db.entities.ExchangeItem
import retrofit2.Callback

class CurrencyRepository(
    private val currencyApi: CurrencyApi,
    private val currencyDao: CurrencyDao,
    private val widgetCurApi: CurrencyWidgetAPI
) {
    val allCurrencyFromDb: LiveData<List<CurrencyItem>> = currencyDao.getAllData()
    val favouritesCurrencies: LiveData<List<CurrencyItem>> = currencyDao.getFavouritesCurrencies()

    fun getAllCurrency(callback: Callback<CurrencyApiResponse>) {
        currencyApi.getAllCurrency().enqueue(callback)
    }

    fun getLatestExchangeRates(callback: Callback<CurrencyExchangeResponse>) {
        currencyApi.getLatestExchangeRates().enqueue(callback)
    }

    fun getCurrencyForSearch(name: String) = currencyDao.getCurrencyForSearch(name)


    suspend fun setDataToCurrencyTable(currencies: List<CurrencyItem>) {
        currencies.forEach {
            currencyDao.addCurrencyData(it)
        }
    }

    suspend fun setDataToExchangeTable(exchange: List<ExchangeItem>) {
        exchange.forEach {
            currencyDao.addExchangeData(it)
        }
    }

    suspend fun updateCurrencyItem(currencyItem: CurrencyItem) {
        currencyDao.updateCurrencyItem(currencyItem)
    }

    suspend fun getExchangeRateForCurrency(code: String) =
        currencyDao.getExchangeRateForCurrency(code)

    suspend fun getCurrencyByCode(code: String) = currencyDao.getCurrencyByCode(code)

    fun convertToDollar(currencyValue: Float, currencyExRates: Float): Float { //??????????
        return currencyValue / currencyExRates
    }

    fun convertDollarToCurrency(dollarValue: Float, currencyExRates: Float): Float { //????????????
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