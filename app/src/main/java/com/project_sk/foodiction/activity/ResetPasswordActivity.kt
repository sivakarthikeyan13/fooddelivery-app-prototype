package com.project_sk.foodiction.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.project_sk.foodiction.R
import com.project_sk.foodiction.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var etOtp: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btSubmit: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOtp = findViewById(R.id.etOtp)
        etPassword = findViewById(R.id.etResetPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btSubmit = findViewById(R.id.btSubmit)

        btSubmit.setOnClickListener {
            val otp = etOtp.text.toString()
            val password = etPassword.text.toString()
            val cPassword = etConfirmPassword.text.toString()
            val mobileNumber = intent.getStringExtra("mobile_number")

            if (password == cPassword) {
                val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", password)
                jsonParams.put("otp", otp)


                if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {
                    val jsonRequest = object :
                        JsonObjectRequest(
                            Request.Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    val rpObject = it.getJSONObject("data")
                                    val success = rpObject.getBoolean("success")
                                    if (success) {
                                        Toast.makeText(
                                            this@ResetPasswordActivity,
                                            rpObject.getString("successMessage"),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val rpIntent =
                                            Intent(
                                                this@ResetPasswordActivity,
                                                LoginActivity::class.java
                                            )

                                        sharedPreferences =
                                            getSharedPreferences(getString(R.string.profile_file_name), Context.MODE_PRIVATE)
                                        sharedPreferences.edit().clear().apply()
                                        startActivity(rpIntent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@ResetPasswordActivity,
                                            rpObject.getString("errorMessage"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        "Some unexpected error as occured1!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
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
                    val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent =
                            Intent(Settings.ACTION_WIRELESS_SETTINGS) //implicient intent to open wireless in settings
                        startActivity(settingsIntent)
                        this@ResetPasswordActivity.finish() //closes the activity so when user re enters it its again created and the list is refreshed
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@ResetPasswordActivity) //closes the app(all its running instances is closed and app closes)
                    }
                    dialog.create()
                    dialog.show()
                }
            } else {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "re-enter the password correctly",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
