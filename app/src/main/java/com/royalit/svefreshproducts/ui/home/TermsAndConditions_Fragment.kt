package com.royalit.svefreshproducts.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.erepairs.app.api.Api
import com.royalit.svefreshproducts.R
import com.royalit.svefreshproducts.api.APIClient
import com.royalit.svefreshproducts.databinding.FragmentPrivacyPolicyBinding
import com.royalit.svefreshproducts.databinding.FragmentTermsAndConditionsBinding
import com.royalit.svefreshproducts.models.PrivacyPoilcy_Response
import com.royalit.svefreshproducts.models.TermsAndConditions_Response
import com.royalit.svefreshproducts.utils.NetWorkConection
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TermsAndConditions_Fragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentTermsAndConditionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            ).get(HomeViewModel::class.java)

        _binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false)
        val root: View = binding.root



        getTermsAndConditions()

        return root
    }


    private fun getTermsAndConditions() {
        try {
            if (NetWorkConection.isNEtworkConnected(activity as Activity)) {
                val apiServices = APIClient.client.create(Api::class.java)
                val call = apiServices.getTermsAndConditions(getString(R.string.api_key))


                call.enqueue(object : Callback<TermsAndConditions_Response> {
                    @SuppressLint("WrongConstant")
                    override fun onResponse(
                        call: Call<TermsAndConditions_Response>,
                        response: Response<TermsAndConditions_Response>
                    ) {
                        Log.e(ContentValues.TAG, response.toString())

                        if (response.isSuccessful) {

                            response.body()?.termsandconditionsResponse?.let { listOfcategories ->
                                // Display the first mobile number in the TextView (for example)
                                if (listOfcategories.isNotEmpty()) {
                                    val htmlText = listOfcategories[0].terms
                                    binding.txtNote.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
                                }
                            } ?: run {
                            }

                        }

                    }

                    override fun onFailure(call: Call<TermsAndConditions_Response>, t: Throwable) {
                        Log.e(ContentValues.TAG, t.toString())
                    }
                })
            } else {
                Toast.makeText(
                    activity as Activity,
                    "Please Check your internet",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}