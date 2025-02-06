package com.royalit.svefreshproducts.models

import com.google.gson.annotations.SerializedName

data class Product_Response(

    @SerializedName("products_id") val products_id: Int,
    @SerializedName("product_num") val product_num: String,
    @SerializedName("product_name") val product_name: String,
    @SerializedName("product_title") val product_title: String,
    @SerializedName("product_image") val product_image: String,
    @SerializedName("stock") val stock: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("sales_price") val sales_price: String,
    @SerializedName("offer_price") val offer_price: String,
    @SerializedName("final_amount") val final_amount: String,
    @SerializedName("max_order_quantity") val max_order_quantity: String?,
    @SerializedName("category_2_price") val category_2_price: String?
)
