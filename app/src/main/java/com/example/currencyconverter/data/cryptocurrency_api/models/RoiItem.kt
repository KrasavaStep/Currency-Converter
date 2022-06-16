package com.example.currencyconverter.data.cryptocurrency_api.models

import com.google.gson.annotations.SerializedName

data class RoiItem(
    @SerializedName("times") var times: Double? = null,
    @SerializedName("currency") var currency: String? = null,
    @SerializedName("percentage") var percentage: Double? = null
)
