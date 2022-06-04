package com.example.currencyconverter.currencylistscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.ResultState
import com.example.currencyconverter.data.Repository
import com.example.currencyconverter.mappers.CurrencyMappers
import com.example.currencyconverter.data.currencyapi.models.CurrencyApiModel
import com.example.currencyconverter.data.currencyapi.models.CurrencyApiResponse
import com.example.currencyconverter.data.currencyapi.models.CurrencyExchangeModel
import com.example.currencyconverter.data.currencyapi.models.CurrencyExchangeResponse
import com.example.currencyconverter.data.db.entities.CurrencyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException

class CurrencyListViewModel(private val repository: Repository) : ViewModel() {

    private val _currenciesResult = MutableLiveData<ResultState<Boolean>>()
    val currenciesResult: LiveData<ResultState<Boolean>> = _currenciesResult

    private var currencies = listOf<CurrencyApiModel>()
    private var rates = listOf<CurrencyExchangeModel>()
    val getCurrenciesFromDb: LiveData<List<CurrencyItem>> = repository.getAllCurrencyFromDb


//    private val _rates = MutableLiveData<ResultState<List<CurrencyExchangeModel>>>()
//    val rates: LiveData<ResultState<List<CurrencyExchangeModel>>> = _rates

    private val currencyApiMappers = CurrencyMappers()

    private fun getAllCurrencies() {
        _currenciesResult.value = ResultState.Loading()
        repository.getAllCurrency(object : Callback<CurrencyApiResponse> {
            override fun onResponse(
                call: Call<CurrencyApiResponse>,
                response: Response<CurrencyApiResponse>
            ) {
//                val result = response.body()?.let {
//                    ResultState.Success(currencyApiMappers.fromHashmapToList(it.data))
//                } ?: ResultState.Error(RuntimeException("Response body is null"))
//
//                _currencies.postValue(result)
                response.body()?.let {
                    currencies = currencyApiMappers.fromHashmapToList(it.data)
                } ?: _currenciesResult.postValue(ResultState.Error(RuntimeException("Response body is null")))
            }

            override fun onFailure(call: Call<CurrencyApiResponse>, t: Throwable) {
               _currenciesResult.postValue(ResultState.Error(t))
            }

        })
    }


    private fun getExchangeRates() {
        //_rates.value = ResultState.Loading()

        repository.getLatestExchangeRates(object : Callback<CurrencyExchangeResponse> {
            override fun onResponse(
                call: Call<CurrencyExchangeResponse>,
                response: Response<CurrencyExchangeResponse>
            ) {
//                val result = response.body()?.let {
//                    ResultState.Success(currencyApiMappers.fromHashmapToList(it.data))
//                } ?: ResultState.Error(RuntimeException("Response body is null"))
//
//                _rates.postValue(result)

                response.body()?.let {
                    rates = currencyApiMappers.fromHashmapToList(it.data)
                } ?: _currenciesResult.postValue(ResultState.Error(RuntimeException("Response body is null")))
            }

            override fun onFailure(call: Call<CurrencyExchangeResponse>, t: Throwable) {
                _currenciesResult.postValue(ResultState.Error(t))
            }
        })
    }

    fun setData(){
        getAllCurrencies()
        getExchangeRates()
        viewModelScope.launch(Dispatchers.IO) {
            repository.setDataToDb(currencies, rates)
            _currenciesResult.postValue(ResultState.Success(true))
        }
    }
}