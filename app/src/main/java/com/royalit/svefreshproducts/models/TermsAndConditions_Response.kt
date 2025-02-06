package com.royalit.svefreshproducts.models

import com.google.gson.annotations.SerializedName

data class TermsAndConditions_Response(
    @SerializedName("Status") val status: Boolean,
    @SerializedName("Message") val message: String,
    @SerializedName("Response") val termsandconditionsResponse: List<TermsAndConditionsResponse>?,
    @SerializedName("code") val code: Int,
)
data class TermsAndConditionsResponse(
    @SerializedName("terms") val terms: String,
)
