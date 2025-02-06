package com.royalit.svefreshproducts.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.royalit.svefreshproducts.databinding.SliderItemBinding
import com.royalit.svefreshproducts.models.Slider_Response

class SliderPagerAdapter(
    private val context: Context,
    private val sliderDataList: List<Slider_Response>,
   // private val viewPager: ViewPager
) : PagerAdapter() {

    override fun getCount(): Int {
        return sliderDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val binding = SliderItemBinding.inflate(inflater, container, false)

        // Load the image using Glide or your preferred image loading library
        Glide.with(context)
            .load(sliderDataList[position].full_path)
            .override( 200)
            .into(binding.imageView)

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}


