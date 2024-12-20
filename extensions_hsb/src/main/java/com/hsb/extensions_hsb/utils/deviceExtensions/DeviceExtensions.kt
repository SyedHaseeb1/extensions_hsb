package com.hsb.extensions_hsb.utils.deviceExtensions

import android.content.Context
import android.os.BatteryManager

object DeviceExtensions {
    // Function to get the battery percentage
    fun Context.getBatteryPercentage(): Int {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return level
    }

}