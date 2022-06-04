package com.example.currencyconverter.mappers

import com.example.currencyconverter.data.currencyapi.models.CurrencyApiModel
import com.example.currencyconverter.data.currencyapi.models.CurrencyExchangeModel
import com.example.currencyconverter.data.db.entities.CurrencyItem

class CurrencyMappers {

    fun <T> fromHashmapToList(data: HashMap<String, T>): List<T> {
        val currencyList = mutableListOf<T>()
        for (i in data) {
            currencyList.add(i.value)
        }
        return currencyList
    }

    fun fromCurrencyApiToDbModel(
        currencies: List<CurrencyApiModel>,
        exRates: List<CurrencyExchangeModel>
    ): List<CurrencyItem>
    {
        val currencyItemList = mutableListOf<CurrencyItem>()
        var i = 0
        while (i < currencies.size || i < exRates.size) {
            val dbModel = CurrencyItem(
                currencies[i].symbol,
                currencies[i].name,
                currencies[i].symbol_native,
                currencies[i].decimal_digits,
                currencies[i].rounding,
                currencies[i].code,
                currencies[i].name_plural,
                exRates[i].value
            )
            i++
            currencyItemList.add(dbModel)
        }

        return currencyItemList
    }
}