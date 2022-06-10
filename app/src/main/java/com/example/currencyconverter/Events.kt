package com.example.currencyconverter

class DataEvent<T> (private val content: T)  {
    private var eventHandled = false

    fun getContentIfNotHandled() : T?{
        return if (!eventHandled) {
            eventHandled = true
            content
        } else {
            null
        }
    }
}