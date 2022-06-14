package com.example.currencyconverter.di

import com.example.currencyconverter.data.Repository
import com.example.currencyconverter.data.cryptocurrency_api.CryptocurrencyApi
import com.example.currencyconverter.data.currency_api.CurrencyApi
import com.example.currencyconverter.data.db.CurrencyDao
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    single<Repository> {
        Repository(
            get<CurrencyApi>(),
            get<CurrencyDao>(),
            get<CryptocurrencyApi>()
        )
    }
}