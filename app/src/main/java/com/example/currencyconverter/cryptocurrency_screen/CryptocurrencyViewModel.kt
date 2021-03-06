package com.example.currencyconverter.cryptocurrency_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.DataEvent
import com.example.currencyconverter.data.CryptoRepository
import com.example.currencyconverter.data.db.entities.CryptocurrencyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CryptocurrencyViewModel(private val repository: CryptoRepository) : ViewModel() {

    private var _result = MutableLiveData<DataEvent<Boolean>>()
    val result: LiveData<DataEvent<Boolean>> = _result

    val allCryptos = repository.allCryptos

    fun getCryptoList() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val cryptoDataList = repository.getCryptoData()

            withContext(Dispatchers.Default) {
                for (item in cryptoDataList) {
                    repository.addCryptoData(
                        CryptocurrencyItem(
                            item.id,
                            item.symbol,
                            item.name,
                            item.image,
                            item.currentPrice,
                            item.marketCap,
                            item.fullyDilutedValuation,
                            item.totalVolume,
                            item.high24h,
                            item.low24h,
                            item.priceChange24h,
                            item.priceChangePercentage24h,
                            item.marketCapChange24h,
                            item.marketCapChangePercentage24h,
                            item.priceChangePercentage1hInCurrency
                        )
                    )
                }
            }
            _result.postValue(DataEvent(true))
        } catch (ex: Exception) {
            _result.postValue(DataEvent(false))
        }
    }

    fun getCryptoForSearch(search: String) : LiveData<List<CryptocurrencyItem>>{
        return if (search.isBlank()){
            repository.allCryptos
        } else {
            repository.getCryptoForSearch("%$search%")
        }
    }
}