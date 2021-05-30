package com.example.teta

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MdnsServiceInfo(val name: String, val ip: String,val port: Int)

class ServiceDiscovery(private val nsdManager: NsdManager, onServiceAddedIP: (MdnsServiceInfo) -> Unit) {

    private val resolveListener = object : NsdManager.ResolveListener {
        override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
            Log.e(DEBUG_TAG, "Resolve failed: $errorCode")
        }
        override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
            Log.e(DEBUG_TAG, "Resolve Succeeded. $serviceInfo")
            serviceInfo?.let {
                onServiceAddedIP(MdnsServiceInfo(it.serviceName, it.host.hostAddress, it.port))
            }
        }
    }
    private val discoveryListener = object : NsdManager.DiscoveryListener {

        override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
            Log.i(DEBUG_TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
            Log.i(DEBUG_TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)

        }

        override fun onDiscoveryStarted(serviceType: String?) {
            Log.i(DEBUG_TAG, "Service discovery started")
        }

        override fun onDiscoveryStopped(serviceType: String?) {
            Log.i(DEBUG_TAG, "Service discovery stopped: $serviceType")
        }

        override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
            Log.i(DEBUG_TAG, "Service discovery success: ${serviceInfo}")
            if (serviceInfo?.serviceName?.contains("ESP32") == true) {
                nsdManager.resolveService(serviceInfo, resolveListener)
            }
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
            Log.e(DEBUG_TAG, "service lost: ${serviceInfo?.serviceName}")
        }
    }

    fun registerDiscoveryService() {
        nsdManager.discoverServices("_http._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }
    fun unregisterDiscoveryService() {
        nsdManager.stopServiceDiscovery(discoveryListener)
    }
}