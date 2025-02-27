package com.example.banner.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.banner.utils.eventbus.NetworkEvent
import com.example.banner.utils.eventbus.WifiEvent
import com.example.banner.utils.log
import org.greenrobot.eventbus.EventBus

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        "BroadcastReceiver:$action".log()
        when(action) {

            "com.wirelessmedia.eserver.master_device_change" -> {
                val masterName = intent.getStringExtra("masterName")
                val masterPwd = intent.getStringExtra("masterPwd")
                "BroadcastReceiver: masterName:$masterName, masterPwd:$masterPwd".log()
                EventBus.getDefault().post(WifiEvent(masterName.toString(),masterPwd.toString()))
            }

            ConnectivityManager.CONNECTIVITY_ACTION -> {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val network = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

                if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    // 网络已连接
                    EventBus.getDefault().post(NetworkEvent(true))
                    when {
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            // Wi-Fi连接
                        }

                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            // 移动数据连接
                        }

                        else -> {

                        }
                    }
                } else {
                    // 网络已断开
                    EventBus.getDefault().post(NetworkEvent(false))
                }
            }

        }

    }
}