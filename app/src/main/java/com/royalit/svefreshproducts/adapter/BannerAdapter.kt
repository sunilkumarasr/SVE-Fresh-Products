package com.royalit.svefreshproducts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.models.SlideModel
import com.royalit.svefreshproducts.R


class BannerAdapter(private val banners: List<String>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.banner_item, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        Glide.with(holder.itemView.context)
            .load(banner)  // Banner URL as a string
            .into(holder.bannerImage)
    }

    override fun getItemCount(): Int = banners.size

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bannerImage: ImageView = view.findViewById(R.id.banner_image)
    }
}