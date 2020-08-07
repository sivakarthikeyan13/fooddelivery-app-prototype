package com.project_sk.foodiction.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.project_sk.foodiction.R
import com.project_sk.foodiction.activity.MainActivity

/**
 * A simple [Fragment] subclass.
 */
class ProfilePageFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtPhNumber: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_page, container, false)

        txtName = view.findViewById(R.id.txtName)
        txtPhNumber = view.findViewById(R.id.txtPhNumber)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)
        sharedPreferences = this.activity!!.getSharedPreferences(
            getString(R.string.profile_file_name),
            Context.MODE_PRIVATE
        )

        txtName.text = sharedPreferences.getString("name", "ERROR!")
        txtPhNumber.text = sharedPreferences.getString("mobile_number", "ERROR!")
        txtEmail.text = sharedPreferences.getString("email", "ERROR!")
        txtAddress.text = sharedPreferences.getString("address", "ERROR!")

        return view
    }
}
