package com.example.currencyconverter.mappers

import com.example.currencyconverter.data.currency_api.models.CurrencyApiModel
import com.example.currencyconverter.data.currency_api.models.CurrencyExchangeModel
import com.example.currencyconverter.data.db.entities.CurrencyItem
import com.example.currencyconverter.data.db.entities.ExchangeItem

class CurrencyMappers {

    fun <T> fromHashmapToList(data: HashMap<String, T>): List<T> {
        val currencyList = mutableListOf<T>()
        for (i in data) {
            currencyList.add(i.value)
        }
        return currencyList
    }

    private fun toCurrencyItem(currency: CurrencyApiModel) = CurrencyItem(
        name = currency.name,
        name_plural = currency.name_plural,
        symbol_native = currency.symbol_native,
        decimal_digits = currency.decimal_digits,
        rounding = currency.rounding,
        code = currency.code,
        symbol = currency.symbol
    )

    fun toCurrencyItemList(list: List<CurrencyApiModel>): List<CurrencyItem> {
        val curItemList = mutableListOf<CurrencyItem>()
        for (item in list) {
            if (item != null) {
                curItemList.add(toCurrencyItem(item))
            }
        }
        return curItemList
    }

    private fun toExchangeItem(exchange: CurrencyExchangeModel) = ExchangeItem(
        code = exchange.code,
        value = exchange.value
    )

    fun toExchangeItemList(list: List<CurrencyExchangeModel>) = list.map {
        toExchangeItem(it)
    }
}