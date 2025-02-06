package com.royalit.svefreshproducts.models

import com.google.gson.annotations.SerializedName

data class PrivacyPoilcy_Response(
    @SerializedName("Status") val status: Boolean,
    @SerializedName("Message") val message: String,
    @SerializedName("Response") val privacyResponse: List<PrivacyResponse>?,
    @SerializedName("code") val code: Int,
)
data class PrivacyResponse(
    @SerializedName("privacy") val privacy: String,
)
