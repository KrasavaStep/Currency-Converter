package com.example.currencyconverter.currency_list_screen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.DataEvent
import com.example.currencyconverter.ResultState
import com.example.currencyconverter.data.Repository
import com.example.currencyconverter.mappers.CurrencyMappers
import com.example.currencyconverter.data.currency_api.models.CurrencyApiModel
import com.example.currencyconverter.data.currency_api.models.CurrencyApiResponse
import com.example.currencyconverter.data.currency_api.models.CurrencyExchangeModel
import com.example.currencyconverter.data.currency_api.models.CurrencyExchangeResponse
import com.example.currencyconverter.data.db.entities.CurrencyItem
import com.example.currencyconverter.data.db.entities.ExchangeItem
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException

class CurrencyListViewModel(private val repository: Repository) : ViewModel() {

    private val _currencies = MutableLiveData<ResultState<List<CurrencyApiModel>>>()
    val currencies: LiveData<ResultState<List<CurrencyApiModel>>> = _currencies

    private val _rates = MutableLiveData<ResultState<List<CurrencyExchangeModel>>>()
    val rates: LiveData<ResultState<List<CurrencyExchangeModel>>> = _rates

    private val currencyApiMappers = CurrencyMappers()

    private val _dataForTwoCurrencies =
        MutableLiveData<DataEvent<HashMap<CurrencyItem, ExchangeItem>>>()
    val dataForTwoCurrencies: LiveData<DataEvent<HashMap<CurrencyItem, ExchangeItem>>> =
        _dataForTwoCurrencies

    private val _errorResult = MutableLiveData<DataEvent<String>>()
    val errorResult: LiveData<DataEvent<String>> = _errorResult

    private val _firstCurrencyCode = MutableLiveData<DataEvent<String>>()
    val firstCurrencyCode: LiveData<DataEvent<String>> = _firstCurrencyCode
    private val _secondCurrencyCode = MutableLiveData<DataEvent<String>>()
    val secondCurrencyCode: LiveData<DataEvent<String>> = _secondCurrencyCode

    private val _firstCurrencyName = MutableLiveData<DataEvent<String>>()
    val firstCurrencyName: LiveData<DataEvent<String>> = _firstCurrencyName
    private val _secondCurrencyName = MutableLiveData<DataEvent<String>>()
    val secondCurrencyName: LiveData<DataEvent<String>> = _secondCurrencyName

    fun getAllCurrencies() {
        _currencies.postValue(ResultState.Loading())
        repository.getAllCurrency(object : Callback<CurrencyApiResponse> {
            override fun onResponse(
                call: Call<CurrencyApiResponse>,
                response: Response<CurrencyApiResponse>
            ) {
                response.body()?.let {
                    val currencyApiModelList = currencyApiMappers.fromHashmapToList(it.data)
                    setDataToCurrencyTable(currencyApiModelList)
                    ResultState.Success(currencyApiModelList)
                } ?: ResultState.Error(RuntimeException("Response body is null"))
                Log.d("MyApp", response.toString())
            }

            override fun onFailure(call: Call<CurrencyApiResponse>, t: Throwable) {
                _currencies.postValue(ResultState.Error(t))
                Log.d("MyApp", t.message.toString())
            }

        })
    }

    fun getCurrenciesFromDb(isFavourite: Boolean) : LiveData<List<CurrencyItem>>{
        return if (isFavourite){
            repository.getFavouritesCurrencies
        } else {
            repository.getAllCurrencyFromDb
        }
    }

    fun updateCurrency(currencyItem: CurrencyItem){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCurrencyItem(currencyItem)
        }
    }

    fun getDataForSearch(search: String) : LiveData<List<CurrencyItem>>{
        return if (search.isBlank()){
            repository.getAllCurrencyFromDb
        } else {
            repository.getDataForSearch("%$search%")
        }
    }


    fun getExchangeRates() {
        _rates.value = ResultState.Loading()
        repository.getLatestExchangeRates(object : Callback<CurrencyExchangeResponse> {
            override fun onResponse(
                call: Call<CurrencyExchangeResponse>,
                response: Response<CurrencyExchangeResponse>
            ) {
                val result = response.body()?.let {
                    val currencyExchangeModelList = currencyApiMappers.fromHashmapToList(it.data)
                    setDataToExchangeTable(currencyExchangeModelList)
                    ResultState.Success(currencyExchangeModelList)
                } ?: ResultState.Error(RuntimeException("Response body is null"))
                _rates.postValue(result)
            }

            override fun onFailure(call: Call<CurrencyExchangeResponse>, t: Throwable) {
                _rates.postValue(ResultState.Error(t))
            }
        })
    }

    private fun setDataToCurrencyTable(currencies: List<CurrencyApiModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setDataToCurrencyTable(currencyApiMappers.toCurrencyItemList(currencies))
        }
    }

    private fun setDataToExchangeTable(exchange: List<CurrencyExchangeModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setDataToExchangeTable(currencyApiMappers.toExchangeItemList(exchange))
        }
    }

    fun getAllDataForTwoCurrencies(curCode1: String, curCode2: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                val data = linkedMapOf<CurrencyItem, ExchangeItem>()
                val firstCurrencyItem = async {
                    return@async repository.getCurrencyByCode(curCode1)
                }
                val firstCurrencyExRates = async {
                    return@async repository.getExchangeRateForCurrency(curCode1)
                }
                val secondCurrencyItem = async {
                    return@async repository.getCurrencyByCode(curCode2)
                }
                val secondCurrencyExRates = async {
                    return@async repository.getExchangeRateForCurrency(curCode2)
                }

                val firstCurItemResult = firstCurrencyItem.await()
                val firstCurExRatesResult = firstCurrencyExRates.await()
                val secondCurItemResult = secondCurrencyItem.await()
                val secondCurExRatesResult = secondCurrencyExRates.await()

                if (firstCurItemResult != null && firstCurExRatesResult != null &&
                    secondCurItemResult != null && secondCurExRatesResult != null
                ) {
                    data[firstCurItemResult] = firstCurExRatesResult
                    data[secondCurItemResult] = secondCurExRatesResult
                    return@withContext data
                } else {
                    return@withContext null
                }
            }
            if (result != null)
                _dataForTwoCurrencies.postValue(DataEvent(result))
            else
                _errorResult.postValue(DataEvent("Something went wrong"))
        }
    }

    private fun convertCurrencies(
        execOrder: Int,
        firstExRates: Float,
        secondExRates: Float,
        firstValue: Float,
        secondValue: Float,
        firstCurCode: String,
        secondCurCode: String
    ): Float {
        return when (execOrder) {
            1 -> {
                convertFirstCurrencyToSecond(
                    firstExRates,
                    secondExRates,
                    firstValue,
                    firstCurCode,
                    secondCurCode
                )
            }
            2 -> {
                convertSecondCurrencyToFirst(
                    firstExRates,
                    secondExRates,
                    secondValue,
                    firstCurCode,
                    secondCurCode
                )
            }
            else -> 0f
        }
    }

    fun setDataForCurrencies(
        data: HashMap<CurrencyItem, ExchangeItem>,
        execOrder: Int,
        currencyValue1: Float,
        currencyValue2: Float
    ): Float {
        var cur1: CurrencyItem? = null
        var cur2: CurrencyItem? = null
        for ((i, cur) in data.keys.withIndex()) {
            if (i == 0) {
                cur1 = cur
                _firstCurrencyCode.postValue(DataEvent(cur.code))
                _firstCurrencyName.postValue(DataEvent(cur.name))
            } else {
                cur2 = cur
                _secondCurrencyCode.postValue(DataEvent(cur.code))
                _secondCurrencyName.postValue(DataEvent(cur.name))
            }
        }
        return convertCurrencies(
            execOrder,
            data[cur1]?.value!!,
            data[cur2]?.value!!,
            currencyValue1,
            currencyValue2,
            cur1?.code!!,
            cur2?.code!!
        )
    }

    private fun convertFirstCurrencyToSecond(
        firstExRates: Float,
        secondExRates: Float,
        firstValue: Float,
        firstCurCode: String,
        secondCurCode: String
    ): Float {
        return when {
            firstCurCode == "USD" -> {
                repository.convertDollarToCurrency(firstValue, secondExRates)
            }
            secondCurCode == "USD" -> {
                repository.convertToDollar(firstValue, firstExRates)
            }
            else -> {
                repository.convertFirstCurrencyToSecond(firstExRates, secondExRates, firstValue)
            }
        }
    }

    private fun convertSecondCurrencyToFirst(
        firstExRates: Float,
        secondExRates: Float,
        secondValue: Float,
        firstCurCode: String,
        secondCurCode: String
    ): Float {
        return when {
            secondCurCode == "USD" -> {
                repository.convertDollarToCurrency(secondValue, firstExRates)
            }
            firstCurCode == "USD" -> {
                repository.convertToDollar(secondValue, secondExRates)
            }
            else -> {
                repository.convertSecondCurrencyToFirst(firstExRates, secondExRates, secondValue)
            }
        }
    }


}