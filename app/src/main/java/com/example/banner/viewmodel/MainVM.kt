package com.example.banner.viewmodel

import android.os.SystemProperties
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import com.example.banner.R
import com.example.banner.adapter.BannerAdapter
import com.example.banner.model.BannerModel
import com.example.banner.utils.IpUtils
import com.example.banner.utils.eventbus.NetworkEvent
import com.example.banner.utils.eventbus.WifiEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainVM : ViewModel()  {

    val mId = MutableLiveData("ID:")
    val mPass = MutableLiveData("PASS:")
    val mWifi = MutableLiveData("Wifi Ip:")
    val mLan = MutableLiveData("Lan Ip:")
    val mTime = MutableLiveData("")

    lateinit var mAdapter: BannerAdapter
    val mBanners = ArrayList<BannerModel>()

    init {
        EventBus.getDefault().register(this)
        initData()
        initShowData()
        showTime()
    }

    private fun initShowData() {
        NetworkUtils.getIPAddressAsync(true) {
            mWifi.value = "Wifi Ip:${IpUtils.getLocalIpAddress()}"
            mLan.value = "Lan Ip:$it"
        }
        val mPair = IpUtils.getIdAndPass()
        mId.value = mPair.first
        mPass.value = mPair.second
    }

    //初始化数据，后续可以将图片更改为从文件拿
    private fun initData() {
        mBanners.add(
            BannerModel(
                R.drawable.image1
            )
        )
        mBanners.add(
            BannerModel(
                R.drawable.image2
            )
        )
        mBanners.add(
            BannerModel(
                R.drawable.image3
            )
        )
        mBanners.add(
            BannerModel(
                R.drawable.image4
            )
        )
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun rcvMsg(event: NetworkEvent) {
        NetworkUtils.getIPAddressAsync(true) {
            mWifi.value = "Wifi Ip:${IpUtils.getLocalIpAddress()}"
            mLan.value = "Lan Ip:$it"
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun rcvMsg(event: WifiEvent) {

    }

    private fun showTime() {
        viewModelScope.launch {
            while (true) {
                mTime.value = getCurTime()
                delay(1000)
            }
        }
    }

    private fun getCurTime(): String {
        val currentTime = Date()
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    fun onClickRefreshPass() {
        SystemProperties.get("sys.str.enter", "")
        SystemProperties.set("sys.str.enter", "")
        val mPair = IpUtils.getIdAndPass()
        mId.value = mPair.first
        mPass.value = mPair.second
    }

}