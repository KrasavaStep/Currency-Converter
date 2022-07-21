package com.example.currencyconverter.di

import com.example.currencyconverter.data.CryptoRepository
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.data.WidgetRepository
import com.example.currencyconverter.data.cryptocurrency_api.CryptocurrencyApi
import com.example.currencyconverter.data.currency_api.CurrencyApi
import com.example.currencyconverter.data.currency_api_widget.CurrencyWidgetAPI
import com.example.currencyconverter.data.db.CurrencyDao
import org.koin.dsl.module

val dataModule = module {

    single<CurrencyRepository> {
        CurrencyRepository(
            get<CurrencyApi>(),
            get<CurrencyDao>(),
            get<CurrencyWidgetAPI>()
        )
    }

    single<CryptoRepository> {
        CryptoRepository(
            get<CurrencyDao>(),
            get<CryptocurrencyApi>()
        )
    }

    single<WidgetRepository> {
        WidgetRepository(
            get<CurrencyWidgetAPI>()
        )
    }
}