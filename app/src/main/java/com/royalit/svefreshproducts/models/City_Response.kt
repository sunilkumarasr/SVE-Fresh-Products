package com.royalit.svefreshproducts.models

import com.google.gson.annotations.SerializedName

data class City_Response(
    @SerializedName("Status") val status: Boolean,
    @SerializedName("Message") val message: String,
    @SerializedName("Response") val response: List<City_List>,
    @SerializedName("code") val code: Int
)

data class City_List(
    @SerializedName("city_id") val city_id : String,
    @SerializedName("city_name") val city_name : String,
    @SerializedName("status") val status : String
)
