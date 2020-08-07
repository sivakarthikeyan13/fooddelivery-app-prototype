package com.project_sk.foodiction.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.project_sk.foodiction.R
import com.project_sk.foodiction.adapter.HomeRecyclerAdapter
import com.project_sk.foodiction.async_task.DBAsyncTask
import com.project_sk.foodiction.database.ResEntity
import com.project_sk.foodiction.database.RestaurantDatabase
import com.project_sk.foodiction.model.Restaurant
import com.project_sk.foodiction.util.ConnectionManager
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class HomePageFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    val resInfoList = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE //to make the progresslayout visible
        layoutManager = LinearLayoutManager(activity) //for linear recyler layout

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {
                    try {
                        progressLayout.visibility =
                            View.GONE //to hide the progresslayout when the info from server is recieved
                        val data = it.getJSONObject("data")
                        val success =
                            data.getBoolean("success")  //it is the variable in which the response   stores as a string
                        if (success) {
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val resJsonObject =
                                    resArray.getJSONObject(i) //getJSONOject() is used to extract the objects
                                val resObject = Restaurant(
                                    resJsonObject.getString("id"),
                                    resJsonObject.getString("name"),
                                    resJsonObject.getString("rating"),
                                    resJsonObject.getString("cost_for_one"),
                                    resJsonObject.getString("image_url")
                                )
                                resInfoList.add(resObject)

                                if (activity != null) {
                                    recyclerAdapter = HomeRecyclerAdapter(
                                        activity as Context,
                                        resInfoList,
                                        false
                                    ) //sending the list to adapter class
                                    recyclerHome.itemAnimator = DefaultItemAnimator()
                                    recyclerHome.adapter = recyclerAdapter
                                    recyclerHome.layoutManager = layoutManager

                                }
                            }

                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error occurred1!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error as occured2!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener { //volley errors are handled here

                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error as occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent =
                    Intent(Settings.ACTION_WIRELESS_SETTINGS) //implicient intent to open wireless in settings
                startActivity(settingsIntent)
                activity?.finish() //closes the activity so when user re enters it its again created and the list is refreshed
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity) //closes the app(all its running instances is closed and app closes)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }
}
