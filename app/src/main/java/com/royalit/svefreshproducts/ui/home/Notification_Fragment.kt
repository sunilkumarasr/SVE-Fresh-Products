package com.royalit.svefreshproducts.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erepairs.app.adapter.NotificationAdapter
import com.erepairs.app.api.Api
import com.royalit.svefreshproducts.R
import com.royalit.svefreshproducts.adapter.OrdersHisList_Adapter
import com.royalit.svefreshproducts.api.APIClient
import com.royalit.svefreshproducts.databinding.FragmentNavigationNotifyBinding
import com.royalit.svefreshproducts.models.NotificationListResponse
import com.royalit.svefreshproducts.models.NotificationModel
import com.royalit.svefreshproducts.models.OrderHis_ListResponse
import com.royalit.svefreshproducts.models.OrderHis_Response
import com.royalit.svefreshproducts.utils.NetWorkConection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class Notification_Fragment : Fragment() {

    private var _binding: FragmentNavigationNotifyBinding? = null
    private val binding get() = _binding!!
    lateinit var root: View

    private lateinit var homeViewModel: HomeViewModel
    lateinit var orderhisadapter: OrdersHisList_Adapter
    lateinit var customerid: String
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        homeViewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            ).get(HomeViewModel::class.java)

        _binding = FragmentNavigationNotifyBinding.inflate(inflater, container, false)
        root = binding.root

        sharedPreferences =
            requireContext().getSharedPreferences(
                "loginprefs",
                Context.MODE_PRIVATE
            )

        customerid = sharedPreferences.getString("userid", "").toString()

        Log.e("customid", "" + customerid)

        getordershisList()

       return  root
    }

    private fun getordershisList() {
        if (NetWorkConection.isNEtworkConnected(activity as Activity)) {
            //Set the Adapter to the RecyclerView//
            var apiServices = APIClient.client.create(Api::class.java)
            val call =
                apiServices.getordershisList(getString(R.string.api_key), customerid)
            binding.orderhisprogress.visibility = View.VISIBLE
            call.enqueue(object : Callback<OrderHis_ListResponse> {
                @SuppressLint("WrongConstant")
                override fun onResponse(
                    call: Call<OrderHis_ListResponse>,
                    response: Response<OrderHis_ListResponse>
                ) {
                    Log.e(ContentValues.TAG, response.toString())
                    binding.orderhisprogress.visibility = View.GONE
                    if (response.isSuccessful) {
                        try {
                            val selectedserviceslist =
                                response.body()?.response!!
                            if (selectedserviceslist.isEmpty()){
                                binding.txtNoData.visibility = View.VISIBLE
                            }else{
                                orderhisadapter =
                                    OrdersHisList_Adapter(
                                        activity as Activity,
                                        selectedserviceslist as ArrayList<OrderHis_Response>
                                    )
                                binding.orderhisRecycler.adapter =
                                    orderhisadapter

                                orderhisadapter.notifyDataSetChanged()
                            }
                        } catch (e: java.lang.NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }
                override fun onFailure(call: Call<OrderHis_ListResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, t.toString())
                    binding.orderhisprogress.visibility = View.GONE
                }
            })
        } else {
            Toast.makeText(
                activity as Activity,
                "Please Check your internet",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

