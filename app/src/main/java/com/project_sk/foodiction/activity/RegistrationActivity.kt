package com.project_sk.foodiction.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.project_sk.foodiction.R
import com.project_sk.foodiction.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject


class RegistrationActivity : AppCompatActivity() {
    lateinit var etname: EditText
    lateinit var etemail: EditText
    lateinit var etregistermobilenumber: EditText
    lateinit var etdeliveryaddress: EditText
    lateinit var etregisterpassword: EditText
    lateinit var etregisterconfirmpassword: EditText
    lateinit var btregister: Button
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etname = findViewById(R.id.etname)
        etemail = findViewById(R.id.etemail)
        etregistermobilenumber = findViewById(R.id.etregistermobilenumber)
        etdeliveryaddress = findViewById(R.id.etdeliveryaddress)
        etregisterpassword = findViewById(R.id.etregisterpassword)
        etregisterconfirmpassword = findViewById(R.id.etregisterconfirmpassword)
        btregister = findViewById(R.id.btregister)
        toolbar = findViewById(R.id.Toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Registration"


        btregister.setOnClickListener {
            val rName = etname.text.toString()
            val rEmail = etemail.text.toString()
            val rMobileNumber = etregistermobilenumber.text.toString()
            val rDeliveryAddress = etdeliveryaddress.text.toString()
            val rPassword = etregisterpassword.text.toString()
            val rConfirmPassword = etregisterconfirmpassword.text.toString()

            if (rPassword == rConfirmPassword) {
                val queue = Volley.newRequestQueue(this@RegistrationActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("name", rName)
                jsonParams.put("mobile_number", rMobileNumber)
                jsonParams.put("password", rPassword)
                jsonParams.put("address", rDeliveryAddress)
                jsonParams.put("email", rEmail)

                if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {
                    val jsonRequest = object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val profileArray = data.getJSONObject("data")
                                    val userId = profileArray.getString("user_id")
                                    val name = profileArray.getString("name")
                                    val email = profileArray.getString("email")
                                    val mobileNumber = profileArray.getString("mobile_number")
                                    val address = profileArray.getString("address")

                                    sharedPreferences = getSharedPreferences(
                                        getString(R.string.profile_file_name),
                                        Context.MODE_PRIVATE
                                    )
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    saveInSharedPreference("user_id", userId)
                                    saveInSharedPreference("name", name)
                                    saveInSharedPreference("email", email)
                                    saveInSharedPreference("mobile_number", mobileNumber)
                                    saveInSharedPreference("address", address)

                                    val registerIntent = Intent(
                                        this@RegistrationActivity,
                                        MainActivity::class.java
                                    )

                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Registered",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(registerIntent)
                                } else {
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        data.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Some unexpected error as occured1!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@RegistrationActivity,
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
                    val dialog = AlertDialog.Builder(this@RegistrationActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent =
                            Intent(Settings.ACTION_WIRELESS_SETTINGS) //implicient intent to open wireless in settings
                        startActivity(settingsIntent)
                        this@RegistrationActivity.finish() //closes the activity so when user re enters it its again created and the list is refreshed
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@RegistrationActivity) //closes the app(all its running instances is closed and app closes)
                    }
                    dialog.create()
                    dialog.show()
                }

            } else {
                Toast.makeText(
                    this@RegistrationActivity,
                    "re-enter the password correctly",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun saveInSharedPreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

}