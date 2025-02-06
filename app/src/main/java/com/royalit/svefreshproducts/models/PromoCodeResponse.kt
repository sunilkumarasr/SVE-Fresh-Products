package com.royalit.svefreshproducts.models

import com.google.gson.annotations.SerializedName
import com.royalit.svefreshproducts.roomdb.CartItems

data class PromoCodeResponse(
    @SerializedName("Status") val status: Boolean,
    @SerializedName("Message") val message: String,
    @SerializedName("PromoCode") val PromoCode: String,
    @SerializedName("code") val code: Int
)
