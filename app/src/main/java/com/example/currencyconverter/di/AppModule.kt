package com.example.currencyconverter.di

import com.example.currencyconverter.crypto_graphic_screen.CryptoGraphicViewModel
import com.example.currencyconverter.cryptocurrency_screen.CryptocurrencyViewModel
import com.example.currencyconverter.currency_list_screen.CurrencyListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    viewModel<CurrencyListViewModel>(named("currencyVM")){
        CurrencyListViewModel(repository = get())
    }

    viewModel<CryptocurrencyViewModel>(named("cryptoVM")){
        CryptocurrencyViewModel(repository = get())
    }

    viewModel<CryptoGraphicViewModel>(named("graphicVM")){
        CryptoGraphicViewModel(repository = get())
    }
}