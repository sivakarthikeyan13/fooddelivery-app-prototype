package com.project_sk.foodiction.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.project_sk.foodiction.R
import com.project_sk.foodiction.adapter.DishRecyclerAdapter
import com.project_sk.foodiction.adapter.HomeRecyclerAdapter
import com.project_sk.foodiction.async_task.DBAsyncTask
import com.project_sk.foodiction.database.ResEntity
import com.project_sk.foodiction.database.RestaurantDatabase
import com.project_sk.foodiction.model.Dish
import com.project_sk.foodiction.model.Restaurant
import com.project_sk.foodiction.util.ConnectionManager
import org.json.JSONException

class RestaurantDetailsActivity : AppCompatActivity() {

    lateinit var recyclerDetails: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DishRecyclerAdapter
    lateinit var imgFav: ImageView
    lateinit var btnProceedToCart: Button
    lateinit var toolbar: Toolbar
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    val dishInfoList = arrayListOf<Dish>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        recyclerDetails = findViewById(R.id.recyclerDetails)
        imgFav = findViewById(R.id.imgFav)
        btnProceedToCart = findViewById(R.id.btProceedToCart)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.Toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("res_name")
        progressLayout.visibility = View.VISIBLE //to make the progresslayout visible

        if (intent.getBooleanExtra("is_fav", false)){
            imgFav.setImageResource(R.drawable.ic_favourite_red)
        }else{
            imgFav.setImageResource(R.drawable.ic_favorite_border)
        }

        layoutManager =
            LinearLayoutManager(this@RestaurantDetailsActivity)//for linear recyler layout


        val url =
            "http://13.235.250.119/v2/restaurants/fetch_result/" + intent.getStringExtra("res_id")
        val queue = Volley.newRequestQueue(this@RestaurantDetailsActivity)
        if (ConnectionManager().checkConnectivity(this@RestaurantDetailsActivity)) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {
                    try {
                        progressLayout.visibility =
                            View.GONE //to hide the progresslayout when the info from server is recieved
                        val data = it.getJSONObject("data")
                        val success =
                            data.getBoolean("success")  //it is the variable in which the response   stores as a string
                        if (success) {
                            val dishArray = data.getJSONArray("data")
                            for (i in 0 until dishArray.length()) {
                                val dishJsonObject =
                                    dishArray.getJSONObject(i) //getJSONOject() is used to extract the objects
                                val dishObject = Dish(
                                    dishJsonObject.getString("id"),
                                    dishJsonObject.getString("name"),
                                    dishJsonObject.getString("cost_for_one"),
                                    dishJsonObject.getString("restaurant_id")
                                )
                                dishInfoList.add(dishObject)
                                recyclerAdapter = DishRecyclerAdapter(
                                    this@RestaurantDetailsActivity,
                                    dishInfoList
                                ) //sending the list to adapter class
                                recyclerDetails.itemAnimator = DefaultItemAnimator()
                                recyclerDetails.adapter = recyclerAdapter
                                recyclerDetails.layoutManager = layoutManager


                            }

                        } else {
                            Toast.makeText(
                                this@RestaurantDetailsActivity,
                                "Some error occurred1!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@RestaurantDetailsActivity,
                            "Some unexpected error as occured2!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener { //volley errors are handled here

                    Toast.makeText(
                        this@RestaurantDetailsActivity,
                        "Volley error as occurred!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()     //hashmap is for kotlin, mutablemap is for java
                    headers["Content-type"] = "application/json"
                    headers["token"] = "3b526a48b3e9f5"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(this@RestaurantDetailsActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent =
                    Intent(Settings.ACTION_WIRELESS_SETTINGS) //implicient intent to open wireless in settings
                startActivity(settingsIntent)
                this@RestaurantDetailsActivity.finish() //closes the activity so when user re enters it its again created and the list is refreshed
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@RestaurantDetailsActivity) //closes the app(all its running instances is closed and app closes)
            }
            dialog.create()
            dialog.show()

        }


        btnProceedToCart.setOnClickListener {
            val cartIntent = Intent(
                this@RestaurantDetailsActivity,
                CartPageActivity::class.java
            )
            cartIntent.putExtra("res_name", intent.getStringExtra("res_name"))
            startActivity(cartIntent)
        }

    }
}
