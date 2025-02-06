package com.royalit.svefreshproducts.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.erepairs.app.api.Api
import com.royalit.svefreshproducts.First_Screen
import com.royalit.svefreshproducts.HomeScreen
import com.royalit.svefreshproducts.R
import com.royalit.svefreshproducts.adapter.BannerAdapter
import com.royalit.svefreshproducts.adapter.CategoryList_Adapter
import com.royalit.svefreshproducts.adapter.HomeCategoryList_Adapter
import com.royalit.svefreshproducts.adapter.ProductList_Adapter
import com.royalit.svefreshproducts.adapter.SliderPagerAdapter
import com.royalit.svefreshproducts.api.APIClient
import com.royalit.svefreshproducts.databinding.FragmentHomeBinding
import com.royalit.svefreshproducts.models.AddtoCartResponse
import com.royalit.svefreshproducts.models.CartListResponse
import com.royalit.svefreshproducts.models.Category_ListResponse
import com.royalit.svefreshproducts.models.Category_Response
import com.royalit.svefreshproducts.models.Product_ListResponse
import com.royalit.svefreshproducts.models.Product_Response
import com.royalit.svefreshproducts.models.Slider_Response
import com.royalit.svefreshproducts.models.Sliderlist_Response
import com.royalit.svefreshproducts.models.UpdateCartResponse
import com.royalit.svefreshproducts.models.UserStatusResponse
import com.royalit.svefreshproducts.roomdb.CartItems
import com.royalit.svefreshproducts.roomdb.CartViewModel
import com.royalit.svefreshproducts.roomdb.MotherChoiceDB
import com.royalit.svefreshproducts.utils.NetWorkConection
import com.royalit.svefreshproducts.utils.Utilities
import com.royalit.svefreshproducts.utils.Utilities.customerid
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask

class HomeFragment : Fragment(), ProductList_Adapter.ProductItemClick {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    lateinit var categoryadapter: HomeCategoryList_Adapter
    lateinit var productadapter: ProductList_Adapter
    lateinit var root: View
    var cartData: List<CartItems> = ArrayList<CartItems>()
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewPager: ViewPager
    private lateinit var sliderAdapter: SliderPagerAdapter
    private val viewPagerHandler = Handler()
    private var sliderDataList: List<Slider_Response> = emptyList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding
    lateinit var motherChoiceDB: MotherChoiceDB
    val imageList = ArrayList<String>() // Create image list
    private lateinit var bannerAdapter: BannerAdapter
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


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        root = binding?.root!!

        sharedPreferences =
            requireContext().getSharedPreferences(
                "loginprefs",
                Context.MODE_PRIVATE
            )

        customerid = sharedPreferences.getString("userid", "")!!

        motherChoiceDB =
            Room.databaseBuilder(activity as Activity, MotherChoiceDB::class.java, "mother-choice")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build()
//        motherChoiceDB = MotherChoiceDB.getInstance(activity as Activity)

        val cartVM = CartViewModel(activity, false)
        cartVM.cartData()
        /*cartVM.getCartItems.observe(requireActivity()) { cartItems -> cartData = cartItems
            if (cartItems.size > 0) {
                binding!!.cartnumber.visibility = View.VISIBLE
                binding!!.cvOneLogin.visibility = View.VISIBLE
                binding?.carttext!!.visibility = View.VISIBLE
                binding?.rlOneLogin!!.visibility = View.VISIBLE
                binding?.cartnumber!!.text = "" + cartItems.size
            } else {
                binding?.cartnumber!!.visibility = View.GONE
                binding?.cvOneLogin!!.visibility = View.GONE
                binding?.carttext!!.visibility = View.GONE
                binding?.rlOneLogin!!.visibility = View.GONE
            }
        }*/
        val viewModel = CartViewModel(activity as Activity)
        viewModel.cartCount()

        getCart()
        getUserStatus()

        getCategories()
        getProductsList()

        binding?.viewall!!.setOnClickListener {
            val navController =
                Navigation.findNavController(
                    context as Activity,
                    R.id.nav_host_fragment_content_home_screen
                )
            navController.navigate(R.id.navigation_viewallproducts)
        }


        binding?.viewallCat!!.setOnClickListener {
            val navController =
                Navigation.findNavController(
                    context as Activity,
                    R.id.nav_host_fragment_content_home_screen)
            navController.navigate(R.id.nav_categories)
        }

        binding?.cardSearch!!.setOnClickListener {
            val navController =
                Navigation.findNavController(
                    context as Activity,
                    R.id.nav_host_fragment_content_home_screen)
            navController.navigate(R.id.nav_search)
        }

        try {
            //val viewModel = CartViewModel(activity as Activity)
            /* viewModel.cartCount()
             viewModel.cartCount.observe(requireActivity()) { cartItems ->
                 if (cartItems.size > 0) {
                     binding!!.cartnumber.visibility = View.VISIBLE
                     binding!!.cvOneLogin.visibility = View.VISIBLE
                     binding?.carttext!!.visibility = View.VISIBLE
                     binding?.rlOneLogin!!.visibility = View.VISIBLE
                     binding?.cartnumber!!.text = "" + cartItems.size
                 } else {
                     binding?.cartnumber!!.visibility = View.GONE
                     binding?.cvOneLogin!!.visibility = View.GONE
                     binding?.carttext!!.visibility = View.GONE
                     binding?.rlOneLogin!!.visibility = View.GONE
                 }

             }*/

        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewpager) // Initialize viewPager

        // Start auto-scrolling after viewPager is initialized
        //viewPagerHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY.toLong())

        // Call fetchSliderData() to fetch data and set up the ViewPager
        fetchSliderData()
        getCart()
    }

    var nextItem = 0
    private val autoScrollRunnable = object : Runnable {
        val scrollDelay = 1000
        override fun run() {
            Log.d("SliderDebug", "Auto-scrolling to next item")
            var currentItem = viewPager.currentItem

            if (currentItem == (viewPager.adapter?.count?.minus(1))) {
                nextItem = 0
            } else {
                nextItem = currentItem + 1
            }
            // val nextItem = if (currentItem < viewPager.adapter?.count ?: 0 - 1) currentItem + 1 else 0
            Log.d("SliderDebug", "Current item: $currentItem, Next item: $nextItem")

            val startTime = System.currentTimeMillis() // Record the start time

            // Delay the setCurrentItem to allow time for the image to load
            /* viewPagerHandler.postDelayed({
                 val endTime = System.currentTimeMillis() // Record the end time after setting the item
                 val timeTaken = endTime - startTime // Calculate the time taken
                 Log.d("ViewPagerTiming", "Time taken to move to next item: $timeTaken ms")
             }, 1000)

             // Set the next item with a slight delay to allow time for the image to load
             viewPagerHandler.postDelayed({
                 viewPager.setCurrentItem(nextItem, true)
             }, 100) */// Adjust this delay as needed
            viewPager.setCurrentItem(nextItem, true)
            // Schedule the next auto-scroll
            //  viewPagerHandler.postDelayed(this, scrollDelay.toLong())
        }
    }

    private val AUTO_SCROLL_DELAY = 1000 // Adjust the delay as needed

    private fun fetchSliderData() {
        val apiService = APIClient.client.create(Api::class.java)

        val apiKey = "61d7193e52d6er4AuKwW0MjIU61d7193e52d6eYAqnHTf7" // Replace with your API key
        val value = "14" // Replace with your category ID
        val type = "text"

        val call = apiService.getSliders(apiKey, value, type)

        call.enqueue(object : Callback<Sliderlist_Response> {
            override fun onResponse(
                call: Call<Sliderlist_Response>,
                response: Response<Sliderlist_Response>
            ) {
                if (response.isSuccessful) {
                    val sliderResponse = response.body()

                    if (sliderResponse?.Status == true) {
                        val sliderDataList = sliderResponse.Response
                        imageList.clear()
                        sliderDataList.forEach {
                            // Check if full_path is not empty, then add the image URL to imageList
                            if (it.full_path.isNotEmpty()) {
                                imageList.add(it.full_path)  // Assuming `full_path` is the URL or path to the image
                            }
                        }
                    }

                    bannerAdapter = BannerAdapter(imageList)
                    binding!!.viewPagerBanner.adapter = bannerAdapter

                    // Auto-scroll setup
                    autoScrollViewPager()

                }
            }
            override fun onFailure(call: Call<Sliderlist_Response>, t: Throwable) {
                // Handle network or API call failure here
                // You can add code here to handle the failure case
            }
        })
    }

    private fun autoScrollViewPager() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = viewPager.currentItem
                val nextItem = if (currentItem == imageList.size - 1) 0 else currentItem + 1
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000) // 3-second delay
            }
        }
        handler.postDelayed(runnable, 3000) // Initial delay before starting the auto-scroll
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

                            // Truncate the list to the first 6 items
                            val truncatedList = selectedserviceslist.take(6)

                            activity?.let {
                                categoryadapter = HomeCategoryList_Adapter(
                                    activity as Activity,
                                    truncatedList as ArrayList<Category_Response>
                                )
                                binding?.categoryList!!.adapter = categoryadapter

                                binding?.categoryList!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                                binding?.categoryList!!.setHasFixedSize(true)

//                                categoryadapter.notifyDataSetChanged()
//                                val layoutManager = GridLayoutManager(
//                                    activity as Activity,
//                                    3,
//                                    RecyclerView.VERTICAL,
//                                    false
//                                )
//                                binding?.categoryList!!.layoutManager = layoutManager
//                                binding?.categoryList!!.setItemViewCacheSize(truncatedList.size)



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

    private fun getProductsList() {
        try {
            if (NetWorkConection.isNEtworkConnected(activity as Activity)) {
                var apiServices = APIClient.client.create(Api::class.java)
                val call =
                    apiServices.getproductsList(getString(R.string.api_key))
                binding?.homeprogress!!.visibility = View.VISIBLE
                call.enqueue(object : Callback<Product_ListResponse> {
                    @SuppressLint("WrongConstant")
                    override fun onResponse(
                        call: Call<Product_ListResponse>,
                        response: Response<Product_ListResponse>
                    ) {
                        Log.e(ContentValues.TAG, response.toString())
                        binding!!.homeprogress.visibility = View.GONE

                        if (response.isSuccessful) {
                            try {
                                val selectedserviceslist = ArrayList<Product_Response>()
                                selectedserviceslist.clear()
                                selectedserviceslist.addAll(response.body()?.response!!)

                                productadapter =
                                    ProductList_Adapter(
                                        activity as Activity,
                                        selectedserviceslist as ArrayList<Product_Response>,
                                        cartData, this@HomeFragment
                                    )
                                binding?.productsList!!.adapter =
                                    productadapter
                                productadapter.notifyDataSetChanged()

                                val layoutManager = GridLayoutManager(
                                    activity as Activity,
                                    2,
                                    RecyclerView.VERTICAL,
                                    false
                                )
                                binding?.productsList!!.layoutManager = layoutManager

                            } catch (e: java.lang.NullPointerException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    override fun onFailure(call: Call<Product_ListResponse>, t: Throwable) {
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
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding
    }

    override fun onProductItemClick(itemsData: Product_Response?) {

    }

    private fun addToCart(itemsData: Product_Response?, cartQty: String?) {

        if (NetWorkConection.isNEtworkConnected(activity as Activity)) {
            val apiServices = APIClient.client.create(Api::class.java)
            var call: Call<AddtoCartResponse>? = null
            call = apiServices.addToCart(
                getString(R.string.api_key),
                customer_id = customerid,
                product_id = itemsData?.products_id.toString(),
                quantity = "1"
            )

            Log.e("cat", "Add to cart products_id" + itemsData?.products_id)
            Log.e("cat", "Add to cart customerid " + customerid)

            call.enqueue(object : Callback<AddtoCartResponse> {
                @SuppressLint("WrongConstant")
                override fun onResponse(
                    call: Call<AddtoCartResponse>,
                    response: Response<AddtoCartResponse>
                ) {
                    Log.e(ContentValues.TAG, response.toString())



                    if (response.isSuccessful) {
                        try {

                            getCart()
                            getProductsList()
                            /* val items = CartItems(
                                 0,
                                 cart_id,
                                 Utilities.customerid,
                                 Utilities.getDeviceID(activity),
                                 AppConstants.PRODUCTS_CART,
                                 itemsData!!.product_name,
                                 itemsData.product_image,
                                 cartQty,
                                 java.lang.String.valueOf(itemsData.sales_price),
                                 java.lang.String.valueOf(itemsData.offer_price),
                                 java.lang.String.valueOf(itemsData.products_id),
                                 itemsData.stock,
                                 itemsData.max_order_quantity,
                                 itemsData.category_2_price,
                                 itemsData.product_name,
                                 itemsData.product_title,
                                 itemsData.quantity
                             )
                             Log.e("stock", "" + itemsData.stock)
                             val viewModel = CartViewModel(activity)
                             viewModel.deleteSingle(items)
                             viewModel.insert(items)*/


                        } catch (e: java.lang.NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<AddtoCartResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, t.toString())
                    getCart()
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

    private fun updateCart(itemsData: Product_Response?, cartQty: String?) {

        if (NetWorkConection.isNEtworkConnected(activity as Activity)) {
            val apiServices = APIClient.client.create(Api::class.java)
            var call = apiServices.updateCart(
                getString(R.string.api_key),
                customer_id = customerid,
                quantity = cartQty!!,
                product_id = itemsData?.products_id.toString()!!
            )

            Log.e("cat", "quantity cartQty" + cartQty + " - " + itemsData?.products_id)

            call?.enqueue(object : Callback<UpdateCartResponse> {
                @SuppressLint("WrongConstant")
                override fun onResponse(
                    call: Call<UpdateCartResponse>,
                    response: Response<UpdateCartResponse>
                ) {
                    Log.e(ContentValues.TAG, response.toString())
                    getCart()
                    if (response.isSuccessful) {

                        try {
                            /*  val items = CartItems(
                                  0,
                                  0,
                                  Utilities.customerid,
                                  Utilities.getDeviceID(activity),
                                  AppConstants.PRODUCTS_CART,
                                  itemsData!!.product_name,
                                  itemsData.product_image,
                                  cartQty,
                                  java.lang.String.valueOf(itemsData.sales_price),
                                  java.lang.String.valueOf(itemsData.offer_price),
                                  java.lang.String.valueOf(itemsData.products_id),
                                  itemsData.stock,
                                  itemsData.max_order_quantity,
                                  itemsData.category_2_price,
                                  itemsData.product_name,
                                  itemsData.product_title,
                                  itemsData.quantity
                              )
                              Log.e("stock", "" + itemsData.stock)
                              val viewModel = CartViewModel(activity)
                              viewModel.insert(items)
  */

                        } catch (e: java.lang.NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<UpdateCartResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, t.toString())
                }
            })
        } else {
            getCart()
            Toast.makeText(
                activity as Activity,
                "Please Check your internet",
                Toast.LENGTH_LONG
            ).show()
        }


    }

    override fun onAddToCartClicked(itemsData: Product_Response?, cartQty: String?, isAdd: Int) {

        if (isAdd == 0) {
            addToCart(itemsData, cartQty)
        } else
            updateCart(itemsData, cartQty)
        /*val items = CartItems(
            0,
            itemsData?.cartID!!,
            Utilities.customerid,
            Utilities.getDeviceID(activity),
            AppConstants.PRODUCTS_CART,
            itemsData?.product_name,
            itemsData?.product_image,
            cartQty,
            java.lang.String.valueOf(itemsData?.sales_price),
            java.lang.String.valueOf(itemsData?.offer_price),
            java.lang.String.valueOf(itemsData?.products_id),
            itemsData?.stock,
            itemsData?.max_order_quantity,
            itemsData?.category_2_price,
            itemsData?.product_name,
            itemsData?.product_title,
            itemsData?.quantity
        )
        val viewModel = CartViewModel(activity)
        viewModel.insert(items)*/
        Toast.makeText(context, "Item added to cart successfully", Toast.LENGTH_LONG).show()
    }

    private fun getCart() {

        if (NetWorkConection.isNEtworkConnected(activity as Activity)) {
            try {
                (requireActivity() as HomeScreen).updateCartCount()
            } catch (e: Exception) {

            }
            val apiServices = APIClient.client.create(Api::class.java)
            var call = apiServices.getCartList(
                getString(R.string.api_key),
                customer_id = Utilities.customerid
            )

            Log.e("cat", " customerid " + Utilities.customerid)

            call?.enqueue(object : Callback<CartListResponse> {
                @SuppressLint("WrongConstant")
                override fun onResponse(
                    call: Call<CartListResponse>,
                    response: Response<CartListResponse>
                ) {
                    Log.e(ContentValues.TAG, response.toString())

                    if (response.isSuccessful) {
                        try {

                            val cartViewModel = CartViewModel(activity as Activity)
                            //cartViewModel.cartData()
                            /* cartViewModel.getCartItems.observe(requireActivity()) {
                                 cartItems -> cartData = cartItems
                                 Log.d("Cartview Items","Cartview Items 1 ${cartItems.size}")
                             }*/

                            binding?.cartnumber!!.visibility = View.GONE
                            binding?.cvOneLogin!!.visibility = View.GONE
                            binding?.carttext!!.visibility = View.GONE
                            binding?.rlOneLogin!!.visibility = View.GONE

                            /*if (response.body()?.cartList?.size!! > 0) {
                                binding!!.cartnumber.visibility = View.VISIBLE
                                binding!!.cvOneLogin.visibility = View.VISIBLE
                                binding?.carttext!!.visibility = View.VISIBLE
                                binding?.rlOneLogin!!.visibility = View.VISIBLE
                                binding?.cartnumber!!.text = "" + response.body()?.cartList?.size
                            } else {
                                binding?.cartnumber!!.visibility = View.GONE
                                binding?.cvOneLogin!!.visibility = View.GONE
                                binding?.carttext!!.visibility = View.GONE
                                binding?.rlOneLogin!!.visibility = View.GONE
                            }*/
                            cartData = response.body()?.cartList!!

                            try {
                                productadapter.notifyDataSetChanged()
                            } catch (e: Exception) {

                            }


                        } catch (e: java.lang.NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<CartListResponse>, t: Throwable) {
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


    }

    private fun getUserStatus() {

        if (NetWorkConection.isNEtworkConnected(activity as Activity)) {

            val apiServices = APIClient.client.create(Api::class.java)
            var call = apiServices.getUserStatus(
                getString(R.string.api_key),
                customer_id = Utilities.customerid
            )

            Log.e("cat", " customerid " + Utilities.customerid)

            call?.enqueue(object : Callback<UserStatusResponse> {
                @SuppressLint("WrongConstant")
                override fun onResponse(
                    call: Call<UserStatusResponse>,
                    response: Response<UserStatusResponse>
                ) {
                    Log.e(ContentValues.TAG, response.toString())

                    if (response.isSuccessful) {
                        try {
                            if (!response.body()?.userResponse?.status.equals("active")) {
                                val editor = sharedPreferences.edit()
                                editor.clear()
                                editor.commit()

                                var intent = Intent(requireActivity(), First_Screen::class.java)
                                activity?.startActivity(intent)
                                activity?.finish()
                                return
                            }
                        } catch (e: java.lang.NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<UserStatusResponse>, t: Throwable) {
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


    }

}


