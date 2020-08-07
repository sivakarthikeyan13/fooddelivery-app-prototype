package com.project_sk.foodiction.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var etphonenumber: EditText
    lateinit var etpassword: EditText
    lateinit var btlogin: Button
    lateinit var txtforgotpassword: TextView
    lateinit var txtregister: TextView
    var loginsuccess: Boolean = false

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.profile_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        setContentView(R.layout.activity_login)


        if (isLoggedIn) {
            val loginIntent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

        etphonenumber = findViewById(R.id.etphonenumber)
        etpassword = findViewById(R.id.etpassword)
        btlogin = findViewById(R.id.btlogin)
        txtforgotpassword = findViewById(R.id.txtforgotpassword)
        txtregister = findViewById(R.id.txtregister)





        btlogin.setOnClickListener {
            val phonenumber = etphonenumber.text.toString()
            val password = etpassword.text.toString()

            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", phonenumber)
            jsonParams.put("password", password)


            if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                val jsonRequest = object :
                    JsonObjectRequest(
                        Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val profileObject = data.getJSONObject("data")

                                    Toast.makeText(
                                        this@LoginActivity,
                                        "successfully logged in",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val userId = profileObject.getString("user_id")
                                    val name = profileObject.getString("name")
                                    val email = profileObject.getString("email")
                                    val mobileNumber = profileObject.getString("mobile_number")
                                    val address = profileObject.getString("address")

                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    saveInSharedPreference("user_id", userId)
                                    saveInSharedPreference("name", name)
                                    saveInSharedPreference("email", email)
                                    saveInSharedPreference("mobile_number", mobileNumber)
                                    saveInSharedPreference("address", address)

                                    loginsuccess = true
                                    val loginIntent =
                                        Intent(this@LoginActivity, MainActivity::class.java)

                                    startActivity(loginIntent)
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        data.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Some unexpected error as occured1!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@LoginActivity,
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
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent =
                        Intent(Settings.ACTION_WIRELESS_SETTINGS) //implicient intent to open wireless in settings
                    startActivity(settingsIntent)
                    this@LoginActivity.finish() //closes the activity so when user re enters it its again created and the list is refreshed
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity) //closes the app(all its running instances is closed and app closes)
                }
                dialog.create()
                dialog.show()
            }


        }

        txtforgotpassword.setOnClickListener {
            val loginintent = Intent(
                this@LoginActivity,
                ForgotPasswordActivity::class.java
            )
            startActivity(loginintent)
        }

        txtregister.setOnClickListener {
            val loginintent = Intent(
                this@LoginActivity,
                RegistrationActivity::class.java
            )
            startActivity(loginintent)
        }
    }

    override fun onPause() {
        super.onPause()
        if (loginsuccess == true) {
            finish()
        }
    }


    fun saveInSharedPreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}
