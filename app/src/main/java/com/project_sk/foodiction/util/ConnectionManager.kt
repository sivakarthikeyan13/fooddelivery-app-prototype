package com.project_sk.foodiction.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {
    fun checkConnectivity(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager //gives info about currently active network
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        if(activeNetwork?.isConnected != null){
            return activeNetwork.isConnected   //returns true if network has internet connection else false
        }else{
            return false             //returns false if it is null
        }

    }
}