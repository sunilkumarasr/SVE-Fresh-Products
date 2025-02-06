package com.royalit.svefreshproducts.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erepairs.app.api.Api
import com.royalit.svefreshproducts.R
import com.royalit.svefreshproducts.adapter.CategoryList_Adapter
import com.royalit.svefreshproducts.api.APIClient
import com.royalit.svefreshproducts.databinding.FragmentCategoriesBinding
import com.royalit.svefreshproducts.databinding.FragmentHomeBinding
import com.royalit.svefreshproducts.models.Category_ListResponse
import com.royalit.svefreshproducts.models.Category_Response
import com.royalit.svefreshproducts.utils.NetWorkConection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoriesFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding
    lateinit var root: View

    lateinit var categoryadapter: CategoryList_Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            ).get(HomeViewModel::class.java)

        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        root = binding?.root!!

        getCategories()

        return root
    }

    private fun getCategories() {
        if (NetWorkConection.isNEtworkConnected(activity as Activity)) {
            var apiServices = APIClient.client.create(Api::class.java)
            val call =
                apiServices.getcategoruesList(getString(R.string.api_key))
            binding?.homeprogress!!.visibility = View.VISIBLE
            call.enqueue(object : Callback<Category_ListResponse> {
                @SuppressLint("WrongConstant")
                override fun onResponse(
                    call: Call<Category_ListResponse>,
                    response: Response<Category_ListResponse>
                ) {
                    Log.e(ContentValues.TAG, response.toString())
                    binding?.homeprogress!!.visibility = View.GONE
                    if (response.isSuccessful) {
                        try {
                            val selectedserviceslist = response.body()?.response!!

                            activity?.let {
                                categoryadapter = CategoryList_Adapter(
                                    activity as Activity,
                                    selectedserviceslist as ArrayList<Category_Response>
                                )
                                binding?.categoryList!!.adapter = categoryadapter

                                categoryadapter.notifyDataSetChanged()
                                val layoutManager = GridLayoutManager(
                                    activity as Activity,
                                    3,
                                    RecyclerView.VERTICAL,
                                    false
                                )
                                binding?.categoryList!!.layoutManager = layoutManager
                                binding?.categoryList!!.setItemViewCacheSize(selectedserviceslist.size)
                            }
                        } catch (e: java.lang.NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<Category_ListResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, t.toString())
                    binding?.homeprogress!!.visibility = View.GONE
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

}