package com.agdemidov.nasaclient.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network

object NetworkManager {
    var isNetworkAvailable: Boolean = false
        @Synchronized get
        @Synchronized set

    private var connectivityManager: ConnectivityManager? = null

    fun initialize(context: Context) {
        if (connectivityManager == null) {
            connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connectivityManager?.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isNetworkAvailable = true
                }

                override fun onLost(network: Network) {
                    isNetworkAvailable = false
                }
            })
        }
    }
}
