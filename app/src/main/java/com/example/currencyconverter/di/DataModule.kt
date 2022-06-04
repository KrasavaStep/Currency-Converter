package com.example.currencyconverter.di

import com.example.currencyconverter.data.Repository
import org.koin.dsl.module

val dataModule = module {

    single<Repository> {
        Repository(get(), get())
    }
}