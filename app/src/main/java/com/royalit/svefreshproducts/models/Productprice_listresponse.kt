package com.royalit.svefreshproducts.models

data class Productprice_listresponse(
    val Message: String,
    val Response: List<Product_priceresponse>,
    val Status: Boolean,
    val code: Int
)