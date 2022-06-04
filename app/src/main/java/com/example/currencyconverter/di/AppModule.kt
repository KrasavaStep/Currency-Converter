package com.example.currencyconverter.di

import com.example.currencyconverter.currencylistscreen.CurrencyListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<CurrencyListViewModel> {
        CurrencyListViewModel(repository = get())
    }
}