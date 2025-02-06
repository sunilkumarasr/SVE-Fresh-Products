package com.erepairs.app.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royalit.svefreshproducts.databinding.ItemNotificationLayoutBinding
import com.royalit.svefreshproducts.models.NotificationModel

class NotificationAdapter(
    val context: Context,
    var itemsData: ArrayList<NotificationModel>,
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    // create an inner class with name ViewHolder
    //It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: ItemNotificationLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    companion object;

    // bind the items with each item of the list languageList which than will be
    // shown in recycler view
    // to keep it simple we are not setting any image data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(itemsData[position].image)
            .override( 200)
            .into(holder.binding.imgNoti)

        holder.binding.txtTitle.text=itemsData[position].title
        holder.binding.txtDesc.text=itemsData[position].body

    }



    // return the size of languageList
    override fun getItemCount(): Int {
        return itemsData.size
    }
}