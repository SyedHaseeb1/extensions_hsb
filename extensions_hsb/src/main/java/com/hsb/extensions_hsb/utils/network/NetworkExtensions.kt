package com.hsb.extensions_hsb.utils.network

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import androidx.appcompat.app.AppCompatActivity.WIFI_SERVICE
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.logIt
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

object NetworkExtensions {

    /**Returns Device Connected Wifi Router Address*/
    fun Context.getDeviceGateway(): String {
        val manager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val dhcp = manager.dhcpInfo
        val address: String = Formatter.formatIpAddress(dhcp.gateway)
        return address // Return null if no IPv4 address is found
    }


    /**Returns If Port is OPEN*/
    fun String.isPortOpen(port: Int, timOut: Int = 5000): Boolean {
        val ipAddress = this
        return try {
            val socket = Socket()
            val socketAddress = InetSocketAddress(ipAddress, port)
            socketAddress.logIt("DEVICE")
            socket.connect(socketAddress, timOut) // 2 seconds timeout
            socket.close()
            socket.logIt("DEICE_SOCKET")
            true
        } catch (e: SocketTimeoutException) {
            e.logIt("ERROR_SOCKET")
            false // Timeout means port is not open
        } catch (e: Exception) {
            e.logIt("ERROR_SOCKET_2")

            false // Any other exception means something went wrong
        }
    }

    fun Context.getDeviceIpAddress(): String? {
        val wifiManager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress
        return if (ipAddress != 0) {
            // Convert integer IP to readable format
            String.format(
                "%d.%d.%d.%d",
                ipAddress and 0xff,
                ipAddress shr 8 and 0xff,
                ipAddress shr 16 and 0xff,
                ipAddress shr 24 and 0xff
            )
        } else {
            null
        }
    }
}