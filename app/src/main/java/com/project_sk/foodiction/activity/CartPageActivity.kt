package com.project_sk.foodiction.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
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
import com.google.gson.Gson
import com.project_sk.foodiction.R
import com.project_sk.foodiction.adapter.DishRecyclerAdapter
import com.project_sk.foodiction.adapter.HomeRecyclerAdapter
import com.project_sk.foodiction.adapter.OrderRecyclerAdapter
import com.project_sk.foodiction.database.OrderDatabase
import com.project_sk.foodiction.database.OrderEntity
import com.project_sk.foodiction.database.ResEntity
import com.project_sk.foodiction.database.RestaurantDatabase
import com.project_sk.foodiction.model.Dish
import com.project_sk.foodiction.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_cart_page.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartPageActivity : AppCompatActivity() {

    lateinit var rlCart: RelativeLayout
    lateinit var rlOrderplaced: RelativeLayout
    lateinit var txtCResName: TextView
    lateinit var recyclerOrder: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderRecyclerAdapter
    lateinit var toolbar: Toolbar
    lateinit var btplaceOrder: Button
    lateinit var btOk: Button


    val orderInfoList = arrayListOf<Dish>()
    val jsonArrayFood = JSONArray()
    var totalCost = 0
    var userId: String? = null
    var resId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_page)

        recyclerOrder = findViewById(R.id.recyclerCart)
        rlCart = findViewById(R.id.rlCart)
        rlOrderplaced = findViewById(R.id.rlOrderPlaced)
        txtCResName = findViewById(R.id.txtCResName)
        btplaceOrder = findViewById(R.id.btPlaceOrder)
        btOk = findViewById(R.id.btOk)
        toolbar = findViewById(R.id.Toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        rlOrderplaced.visibility = View.GONE
        rlCart.visibility = View.VISIBLE

        txtCResName.text = intent.getStringExtra("res_name")


        layoutManager =
            LinearLayoutManager(this@CartPageActivity) //for linear recyler layout

        val sharedPreferences = getSharedPreferences(
            getString(R.string.profile_file_name),
            Context.MODE_PRIVATE
        )
        userId = sharedPreferences.getString("user_id", "no Name")

        val orderList = CartAsync(this@CartPageActivity).execute().get()

        for (i in 0 until orderList.size) {

            val order = Gson().fromJson(orderList[i].foodItem, Dish::class.java)
            totalCost += order.dishCost.toInt()
            resId = order.resId
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[i].dish_id.toString())
            jsonArrayFood.put(i, foodId)

            orderInfoList.add(
                Dish(
                    orderList[i].dish_id.toString(),
                    order.dishName,
                    order.dishCost,
                    order.resId
                )
            )
        }

        recyclerAdapter = OrderRecyclerAdapter(
            this@CartPageActivity,
            orderInfoList
        ) //sending the list to adapter class

        recyclerOrder.layoutManager = layoutManager
        recyclerOrder.itemAnimator = DefaultItemAnimator()
        recyclerOrder.adapter = recyclerAdapter
        recyclerOrder.setHasFixedSize(true)

        btplaceOrder.text = "Place Order(Total Rs:$totalCost)"

        btplaceOrder.setOnClickListener {

            val queue = Volley.newRequestQueue(this@CartPageActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"


            val jsonParams = JSONObject()
            jsonParams.put("user_id", userId)
            jsonParams.put("restaurant_id", resId)
            jsonParams.put("total_cost", totalCost.toString())
            jsonParams.put("food", jsonArrayFood)

            if (ConnectionManager().checkConnectivity(this@CartPageActivity)) {
                val jsonRequest = object :
                    JsonObjectRequest(
                        Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    rlOrderplaced.visibility = View.VISIBLE
                                    rlCart.visibility = View.GONE
                                    Toast.makeText(
                                        this@CartPageActivity,
                                        "Order placed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val delCart = DelCartAsync(this@CartPageActivity).execute().get()

                                } else {
                                    Toast.makeText(
                                        this@CartPageActivity,
                                        data.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@CartPageActivity,
                                    "Some unexpected error as occured1!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@CartPageActivity,
                                "Volley error $it",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "3b526a48b3e9f5"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            } else { //if no internet connection
                val dialog = AlertDialog.Builder(this@CartPageActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent =
                        Intent(Settings.ACTION_WIRELESS_SETTINGS) //implicient intent to open wireless in settings
                    startActivity(settingsIntent)
                    this@CartPageActivity.finish() //closes the activity so when user re enters it its again created and the list is refreshed
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@CartPageActivity) //closes the app(all its running instances is closed and app closes)
                }
                dialog.create()
                dialog.show()
            }

        }

        btOk.setOnClickListener {
            val intent = Intent(
                this@CartPageActivity,
                MainActivity::class.java
            )
            startActivity(intent)
            ActivityCompat.finishAffinity(this@CartPageActivity)
        }

    }

    class CartAsync(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {

            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db")
                .build() //intialise the database

                    return db.orderDao().getAllDish()
        }

    }

    class DelCartAsync(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {

            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db")
                .build() //intialise the database
            db.orderDao().delAllDish()
            return true
        }

    }
}
