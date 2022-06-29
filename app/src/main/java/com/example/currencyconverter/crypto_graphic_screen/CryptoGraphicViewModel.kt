package com.example.currencyconverter.crypto_graphic_screen

import android.app.Application
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.currencyconverter.DataEvent
import com.example.currencyconverter.R
import com.example.currencyconverter.ResourcesProvider
import com.example.currencyconverter.ResultState
import com.example.currencyconverter.data.Repository
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class CryptoGraphicViewModel(private val repository: Repository, private val resourcesProvider: ResourcesProvider) : ViewModel() {

    private val _dayOHLC = MutableLiveData<ResultState<List<List<Double>>>>()
    val dayOHLC: LiveData<ResultState<List<List<Double>>>> = _dayOHLC

    private val _yearOHLC = MutableLiveData<ResultState<List<List<Double>>>>()
    val yearOHLC: LiveData<ResultState<List<List<Double>>>> = _yearOHLC

    private val _dayOHLCData = MutableLiveData<DataEvent<LineData>>()
    val dayOHLCData: LiveData<DataEvent<LineData>> = _dayOHLCData

    private val _yearOHLCData = MutableLiveData<DataEvent<LineData>>()
    val yearOHLCData: LiveData<DataEvent<LineData>> = _yearOHLCData

    fun getDayOHLC(id: String) = viewModelScope.launch(Dispatchers.IO) {
        _dayOHLC.postValue(ResultState.Loading())
        try {
            _dayOHLC.postValue(ResultState.Success(repository.getOHLCForDay(id)))
        } catch (ex: Exception) {
            _dayOHLC.postValue(ResultState.Error(ex))
        }
    }

    fun getYearOHLC(id: String) = viewModelScope.launch(Dispatchers.IO) {
        _yearOHLC.postValue(ResultState.Loading())
        try {
            _yearOHLC.postValue(ResultState.Success(repository.getOHLCForYear(id)))
        } catch (ex: Exception) {
            _yearOHLC.postValue(ResultState.Error(ex))
        }
    }

    fun getDayOHLCData(data: List<List<Double>>) = viewModelScope.launch(Dispatchers.Default) {
        val timeListDef = async {
            val list = ArrayList<String>()
            data.forEach {
                list.add(convertFromUnixToDate(it[0].toLong(), FORMAT_NUMBER_FOR_DAY))
            }
            return@async list
        }

        val priceListDef = async {
            val list = ArrayList<Entry>()
            for ((i, item) in data.withIndex()) {
                list.add(Entry(item[2].toFloat(), i))
            }
            return@async list
        }

        val timeList: ArrayList<String> = timeListDef.await()
        val priceList: ArrayList<Entry> = priceListDef.await()

        val lineDataSet =
            LineDataSet(priceList, resourcesProvider.getString(R.string.chart_desc))
        lineDataSet.color = Color.rgb(45, 120, 179)
        lineDataSet.circleRadius = 0f
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = Color.rgb(77, 176, 255)
        lineDataSet.fillAlpha = 30
        val lineData = LineData(timeList, lineDataSet)
        _dayOHLCData.postValue(DataEvent(lineData))
    }

    fun getYearOHLCData(data: List<List<Double>>) = viewModelScope.launch(Dispatchers.Default) {
        val timeListDef = async {
            val list = ArrayList<String>()
            data.forEach {
                list.add(convertFromUnixToDate(it[0].toLong(), FORMAT_NUMBER_FOR_YEAR))
            }
            return@async list
        }

        val priceListDef = async {
            val list = ArrayList<Entry>()
            for ((i, item) in data.withIndex()) {
                list.add(Entry(item[2].toFloat(), i))
            }
            return@async list
        }

        val timeList: ArrayList<String> = timeListDef.await()
        val priceList: ArrayList<Entry> = priceListDef.await()

        val lineDataSet =
            LineDataSet(priceList, resourcesProvider.getString(R.string.chart_desc))
        lineDataSet.color = Color.rgb(45, 120, 179)
        lineDataSet.circleRadius = 0f
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = Color.rgb(77, 176, 255)
        lineDataSet.fillAlpha = 30
        val lineData = LineData(timeList, lineDataSet)
        _yearOHLCData.postValue(DataEvent(lineData))

    }

    private fun convertFromUnixToDate(unix: Long, formatNumber: Int): String {
        val date = Date(unix)
        val sdf = if (formatNumber == FORMAT_NUMBER_FOR_YEAR) {
            SimpleDateFormat(DATE_FORMAT_FOR_YEAR, Locale.US)
        } else {
            SimpleDateFormat(DATE_FORMAT_FOR_DAY, Locale.US)
        }
        sdf.timeZone = TimeZone.getTimeZone(TIME_ZONE)
        return sdf.format(date)
    }

    companion object {
        private const val DATE_FORMAT_FOR_YEAR = "yyyy-MM-dd"
        private const val FORMAT_NUMBER_FOR_YEAR = 1
        private const val DATE_FORMAT_FOR_DAY = "HH:mm"
        private const val FORMAT_NUMBER_FOR_DAY = 2
        private const val TIME_ZONE = "GMT-4"
    }
}