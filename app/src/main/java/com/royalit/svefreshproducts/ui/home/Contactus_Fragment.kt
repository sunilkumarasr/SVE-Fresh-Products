package com.royalit.svefreshproducts.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.erepairs.app.api.Api
import com.royalit.svefreshproducts.R
import com.royalit.svefreshproducts.api.APIClient
import com.royalit.svefreshproducts.databinding.ContactusScreenBinding
import com.royalit.svefreshproducts.models.Contact_Response
import com.royalit.svefreshproducts.models.LoginList_Response
import com.royalit.svefreshproducts.utils.NetWorkConection
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Contactus_Fragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: ContactusScreenBinding? = null

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

        _binding = ContactusScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.DialIc.setOnClickListener {

//            val callIntent = Intent(Intent.ACTION_DIAL)
//            callIntent.data = Uri.parse("tel:" + 9154102137) //change the number.
//            if (Build.VERSION.SDK_INT > 23) {
//                startActivity(callIntent)
//            } else {
//                if (ActivityCompat.checkSelfPermission(
//                        activity as Activity,
//                        Manifest.permission.CALL_PHONE
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    Toast.makeText(
//                        activity as Activity,
//                        "Permission Not Granted ",
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                } else {
//                    val PERMISSIONS_STORAGE = arrayOf<String>(Manifest.permission.CALL_PHONE)
//                    ActivityCompat.requestPermissions(activity as Activity, PERMISSIONS_STORAGE, 9)
//                    startActivity(callIntent)
//                }
//            }
        }
        binding.phone2.setOnClickListener {

           // onCall()
        }


        val submitButton: Button = binding.contactusBtn
        submitButton.visibility=View.GONE
        submitButton.setOnClickListener {
            // Handle the click event here
            // You can perform actions such as submitting data or navigating to another screen
            // For example, you can show a toast message for demonstration purposes:
            Toast.makeText(requireContext(), "SUBMIT button clicked", Toast.LENGTH_SHORT).show()
        }


        getContacts()

        return root
    }


    private fun getContacts() {
        try {
            if (NetWorkConection.isNEtworkConnected(activity as Activity)) {
                val apiServices = APIClient.client.create(Api::class.java)
                val call = apiServices.getContact(getString(R.string.api_key))


                call.enqueue(object : Callback<Contact_Response> {
                    @SuppressLint("WrongConstant")
                    override fun onResponse(
                        call: Call<Contact_Response>,
                        response: Response<Contact_Response>
                    ) {
                        Log.e(ContentValues.TAG, response.toString())

                        if (response.isSuccessful) {

                            response.body()?.contactResponse?.let { listOfcategories ->
                                // Display the first mobile number in the TextView (for example)
                                if (listOfcategories.isNotEmpty()) {
                                    val allMobileNumbers = listOfcategories.joinToString("\n") { it.mobileno }
                                    binding.phone2.setText(allMobileNumbers)
                                } else {
                                    binding.phone2.text = "No data available"
                                }
                            } ?: run {
                                // Handle null response or failure
                                binding.phone2.text = "Response is null or invalid"
                            }

                        }


                    }

                    override fun onFailure(call: Call<Contact_Response>, t: Throwable) {
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

    fun onCall() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(activity as Activity, Manifest.permission.CALL_PHONE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity as Activity, arrayOf(Manifest.permission.CALL_PHONE),
                123
            )
        } else {
            startActivity(Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:9849566813")))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}