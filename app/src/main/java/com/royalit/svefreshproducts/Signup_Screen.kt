package com.royalit.svefreshproducts

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.erepairs.app.api.Api
import com.royalit.svefreshproducts.api.APIClient
import com.royalit.svefreshproducts.databinding.SignupScreenBinding
import com.royalit.svefreshproducts.models.City_Response
import com.royalit.svefreshproducts.models.SignupList_Response
import com.royalit.svefreshproducts.models.State_Response
import com.royalit.svefreshproducts.utils.NetWorkConection
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Signup_Screen : Activity() {

    private lateinit var binding: SignupScreenBinding
    var pattern =
        "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
    lateinit var name_strng: String
    lateinit var phone_strng: String
    lateinit var email_strng: String
    lateinit var password_strng: String
    lateinit var cnfrmpassword_strng: String
    lateinit var address: String

    lateinit var stateName: String
    lateinit var cityName: String
    var emailPatter = Patterns.EMAIL_ADDRESS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.siginBtn.setOnClickListener {
            val intent = Intent(
                this@Signup_Screen,
                SignIn_Screen::class.java
            )
            startActivity(intent)
            finish()
        }

        StateApi()


        binding.signupBtn.setOnClickListener {
            name_strng = binding.usernameEdit.text.toString()
            phone_strng = binding.phoneEdit.text.toString()
            email_strng = binding.emailEdit.text.toString()
            address = binding.addressEdit.text.toString()
            password_strng = binding.passwordEdit.text.toString()
            cnfrmpassword_strng = binding.confirmpasswordEdit.text.toString()

            if (name_strng.isEmpty()) {
                binding.usernameEdit.error = "Please Enter Name"
            } else if (phone_strng.isEmpty()) {
                binding.phoneEdit.error = "Please Enter Phone Number"
            } else if (phone_strng.length < 10) {
                binding.phoneEdit.error = "Please Enter 10 digits Phone Number"
            } else if (!phone_strng.matches(pattern.toRegex())) {
                binding.phoneEdit.error = "Please Enter valid Phone Number"
            } else if (email_strng.isEmpty()) {
                binding.emailEdit.error = "Please Enter Email"
            } else if (!emailPatter.matcher(email_strng).matches()) {
                binding.emailEdit.error = "Please Enter Valid Email"
            } else if (stateName.equals("")) {
                Toast.makeText(this, "Please select State", Toast.LENGTH_LONG).show()
            } else if (cityName.equals("")) {
                Toast.makeText(this, "Please select City", Toast.LENGTH_LONG).show()
            } else if (address.isEmpty()) {
                binding.addressEdit.error = "Please Enter Address"
            } else if (password_strng.isEmpty()) {
                binding.passwordEdit.error = "Please Enter Password"
            } else if (cnfrmpassword_strng.isEmpty()) {
                binding.confirmpasswordEdit.error = "Please Enter Confirm Password"
            } else if (!password_strng.equals(cnfrmpassword_strng)) {
                binding.confirmpasswordEdit.error = "Password doesn't matched"
            } else if (!binding.termscheck.isChecked) {
                Toast.makeText(this, "Please accept Terms and Conditions", Toast.LENGTH_LONG).show()
            } else {
                postRegister()
            }
        }


    }

    private fun StateApi() {
        try {
            if (NetWorkConection.isNEtworkConnected(this)) {
                var apiServices = APIClient.client.create(Api::class.java)
                val call =
                    apiServices.satateList(
                        getString(R.string.api_key)
                    )
                call.enqueue(object : Callback<State_Response> {
                    override fun onResponse(
                        call: Call<State_Response>,
                        response: Response<State_Response>
                    ) {
                        try {
                            if (response.body()?.status==true) {
                                val stateList = response.body()?.response ?: emptyList()
                                // Update the spinner
                                val adapter = ArrayAdapter(
                                    this@Signup_Screen,
                                    android.R.layout.simple_spinner_item,
                                    stateList.map { it.state_name }
                                )
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.spinnerState.adapter = adapter

                                // Optional: Handle item selection
                                binding.spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedState = stateList[position]
                                        val stateId = selectedState.state_id
                                        stateName = selectedState.state_name

                                        CityApi(stateId)
                                    }

                                    override fun onNothingSelected(p0: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@Signup_Screen,
                                    "" + response.body()?.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        } catch (e: TypeCastException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                    override fun onFailure(call: Call<State_Response>, t: Throwable) {
                        Toast.makeText(
                            this@Signup_Screen,
                            "No State List",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            } else {
                Toast.makeText(this, "Please Check your internet", Toast.LENGTH_LONG).show()
            }
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
        }
    }
    private fun CityApi(stateId: String) {
        try {
            if (NetWorkConection.isNEtworkConnected(this)) {
                var apiServices = APIClient.client.create(Api::class.java)
                val call =
                    apiServices.cityList(
                        getString(R.string.api_key),stateId
                    )
                call.enqueue(object : Callback<City_Response> {
                    override fun onResponse(
                        call: Call<City_Response>,
                        response: Response<City_Response>
                    ) {
                        try {
                            if (response.body()?.status==true) {
                                val stateList = response.body()?.response ?: emptyList()
                                // Update the spinner
                                val adapter = ArrayAdapter(
                                    this@Signup_Screen,
                                    android.R.layout.simple_spinner_item,
                                    stateList.map { it.city_name }
                                )
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.spinnerCity.adapter = adapter

                                // Optional: Handle item selection
                                binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedState = stateList[position]
                                        val city_id = selectedState.city_id
                                        cityName = selectedState.city_name

                                    }

                                    override fun onNothingSelected(p0: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@Signup_Screen,
                                    "" + response.body()?.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        } catch (e: TypeCastException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                    override fun onFailure(call: Call<City_Response>, t: Throwable) {
                        Toast.makeText(
                            this@Signup_Screen,
                            "No State List",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            } else {
                Toast.makeText(this, "Please Check your internet", Toast.LENGTH_LONG).show()
            }
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
        }
    }


    private fun postRegister() {
        try {
            if (NetWorkConection.isNEtworkConnected(this)) {
                binding.progressBarregister.visibility = View.VISIBLE
                var apiServices = APIClient.client.create(Api::class.java)
                val call =
                    apiServices.user_registation(
                        getString(R.string.api_key),
                        name_strng,
                        phone_strng, email_strng, stateName, cityName, password_strng,  address
                    )
                Log.e("nme", "" + name_strng + email_strng)
                Log.e("email", "" + phone_strng + password_strng)
                call.enqueue(object : Callback<SignupList_Response> {
                    override fun onResponse(
                        call: Call<SignupList_Response>,
                        response: Response<SignupList_Response>
                    ) {
                        binding.progressBarregister.visibility = View.GONE
                        Log.e(ContentValues.TAG, response.toString())
                        try {
                            if (response.body()?.code == 1) {
                                Toast.makeText(
                                    this@Signup_Screen,
                                    "" + response.body()!!.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(this@Signup_Screen, SignIn_Screen::class.java)
                                startActivity(intent)
                                finish()
                            } else if (response.body()?.code == 3) {
                                Toast.makeText(
                                    this@Signup_Screen,
                                    "" + response.body()?.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        } catch (e: TypeCastException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<SignupList_Response>, t: Throwable) {
                        binding.progressBarregister.visibility = View.GONE
                        Log.e(ContentValues.TAG, t.localizedMessage)
                        Toast.makeText(
                            this@Signup_Screen,
                            "Mobile Already Exist!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            } else {
                Toast.makeText(this, "Please Check your internet", Toast.LENGTH_LONG).show()
            }
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
        }
    }


}