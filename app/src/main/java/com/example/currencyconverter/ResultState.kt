package com.example.currencyconverter

sealed class ResultState<T> {
    class Loading<T> : ResultState<T>()
    class Error<T>(val exception: Throwable): ResultState<T>()
    class Success<T>(val data: T): ResultState<T>()
}