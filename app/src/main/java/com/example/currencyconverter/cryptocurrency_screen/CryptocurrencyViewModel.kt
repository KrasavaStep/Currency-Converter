package com.example.currencyconverter.cryptocurrency_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.DataEvent
import com.example.currencyconverter.data.Repository
import com.example.currencyconverter.data.cryptocurrency_api.models.crypto_data_models.CryptoDataModel
import com.example.currencyconverter.data.db.entities.CryptocurrencyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CryptocurrencyViewModel(private val repository: Repository) : ViewModel() {

    private var _result = MutableLiveData<DataEvent<Boolean>>()
    val result: LiveData<DataEvent<Boolean>> = _result

    fun getCryptoList() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val cryptoList = repository.getCryptoList()
            val priceList = async {
                val list = mutableListOf<Float>()
                for ((i, item) in cryptoList.withIndex()){
                    if (i == MAX_QUERY_COUNT) break
                    val price = repository.getCryptoPrices(item.id)[item.id]?.usd
                    if (price != null) {
                        list.add(price)
                    }
                }
                return@async list
            }.await()

            val dataList = async {
                val list = mutableListOf<CryptoDataModel>()
                for ((i,item) in cryptoList.withIndex()) {
                    if (i == MAX_QUERY_COUNT) break
                    val data = repository.getCryptoData(item.id)
                    val homepageLink = data.links.homepage[FIRST_LIST_ELEMENT]
                    val blockchainLink = data.links.blockchain_site[FIRST_LIST_ELEMENT]
                    val forum = data.links.official_forum_url[FIRST_LIST_ELEMENT]
                    val twitter = "$TWITTER_URL${data.links.twitter_screen_name}"
                    val imageLink = data.image[THUMB_IMG_KEY]

                    if(!imageLink.isNullOrEmpty()){
                        list.add(CryptoDataModel(homepageLink, blockchainLink, forum, twitter, imageLink))
                    }
                }
                return@async list
            }.await()

            withContext(Dispatchers.Default) {
                for ((i, item) in cryptoList.withIndex()) {
                    repository.addCryptoData(
                        CryptocurrencyItem(
                            id = cryptoList[i].id,
                            symbol = cryptoList[i].symbol,
                            name = cryptoList[i].name,
                            price = priceList[i],
                            homepageLink = dataList[i].homepageLink,
                            blockchainLink = dataList[i].blockchainLink,
                            forum = dataList[i].forum,
                            twitter = dataList[i].twitter,
                            imageLink = dataList[i].imageLink
                        )
                    )
                }
            }
            _result.postValue(DataEvent(true))
        } catch (ex: Exception) {
            _result.postValue(DataEvent(false))
        }
    }

    companion object {
        private const val TWITTER_URL = "https://twitter.com/"
        private const val THUMB_IMG_KEY = "thumb"
        private const val FIRST_LIST_ELEMENT = 0
        private const val MAX_QUERY_COUNT = 20
    }
}