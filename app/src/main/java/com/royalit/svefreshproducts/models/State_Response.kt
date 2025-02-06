package com.royalit.svefreshproducts.models

import com.google.gson.annotations.SerializedName

data class State_Response(
    @SerializedName("Status") val status: Boolean,
    @SerializedName("Message") val message: String,
    @SerializedName("Response") val response: List<State_List>,
    @SerializedName("code") val code: Int
)

data class State_List(
    @SerializedName("state_id") val state_id : String,
    @SerializedName("state_name") val state_name : String,
    @SerializedName("status") val status : String
)