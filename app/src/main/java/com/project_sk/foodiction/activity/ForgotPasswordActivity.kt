package com.project_sk.foodiction.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity(){
    lateinit var etforgotpasswordphonenumber: EditText
    lateinit var etforgotpasswordemail: EditText
    lateinit var btnext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etforgotpasswordphonenumber = findViewById(R.id.etforgotpasswordphonenumber)
        etforgotpasswordemail = findViewById(R.id.etforgotpasswordemail)
        btnext = findViewById(R.id.btnext)

        btnext.setOnClickListener {
            val fpphonenumber = etforgotpasswordphonenumber.text.toString()
            val fpemail = etforgotpasswordemail.text.toString()

            val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", fpphonenumber)
            jsonParams.put("email", fpemail)


            if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
                val jsonRequest = object :
                    JsonObjectRequest(
                        Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val fpObject = it.getJSONObject("data")
                                val success = fpObject.getBoolean("success")
                                if (success) {
                                    val firstTry = fpObject.getBoolean("first_try")
                                    if (!firstTry) {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "Use the same OTP you received before.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    val fpIntent =
                                        Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                                    fpIntent.putExtra("mobile_number", fpphonenumber)
                                    startActivity(fpIntent)
                                } else {
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        fpObject.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Some unexpected error as occured1!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
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
                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent =
                        Intent(Settings.ACTION_WIRELESS_SETTINGS) //implicient intent to open wireless in settings
                    startActivity(settingsIntent)
                    this@ForgotPasswordActivity.finish() //closes the activity so when user re enters it its again created and the list is refreshed
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity) //closes the app(all its running instances is closed and app closes)
                }
                dialog.create()
                dialog.show()
            }

        }
    }
}