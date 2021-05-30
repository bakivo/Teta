package com.example.teta

import android.app.Application
import android.content.Context
import android.net.nsd.NsdManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val ESP_SERVICE_NAME = "ESP32-WebServer"
const val NOT_SELECTED = -1
data class Esp32Unit(val name: String, val ip: String)

class TetaViewModel(application: Application) : AndroidViewModel(application) {

    var espUnits: List<Esp32Unit> by mutableStateOf(listOf())
        private set
    var currentPosition by mutableStateOf(NOT_SELECTED)
    val currentUnit: Esp32Unit?
        get() = espUnits.getOrNull(currentPosition)

    var color: Color by mutableStateOf(Color.Magenta)
        private set
    var hue: Float by mutableStateOf(0f)
        private set
    var saturation: Float by mutableStateOf(0f)
        private set
    var lightness: Float by mutableStateOf(0f)
        private set

    private val hslArray = FloatArray(3)
    private var mdnsDiscovery: ServiceDiscovery

    init {
        setup()
        val nsdManager = application.applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
        mdnsDiscovery = ServiceDiscovery(nsdManager = nsdManager, onServiceAddedIP = this::onServiceDiscoveryResolved)
        viewModelScope.launch {
            delay(5000)
            repeat(3) {
                espUnits = espUnits + listOf(Esp32Unit("esp", "$it"))
                delay(1000)
            }
            delay(3000)
            //espUnits = listOf()
        }
    }
    // ***************** GUI **********************************************
    private fun setup() {
        hue = 180f; saturation = 0.7f; lightness = 0.45f
        color = updateColor()
    }
    private fun updateColor(): Color {
        return Color(ColorUtils.HSLToColor(hslArray.apply { this[0] = hue; this[1] = saturation ; this[2] = lightness }))
    }

    fun onHueChange(hue: Float) {
        this.hue = hue
        color = updateColor()
    }
    fun onSaturationChange(saturation: Float) {
        this.saturation = saturation
        color = updateColor()
    }
    fun onLightnessChange(lightness: Float) {
        this.lightness = lightness
        color = updateColor()
    }
    // ************** Service Discovery ***************
    fun startServiceDiscovery() {
        viewModelScope.launch {
            mdnsDiscovery.registerDiscoveryService()
        }
    }
    fun stopServiceDiscovery() {
        mdnsDiscovery.unregisterDiscoveryService()
    }

    private fun onServiceDiscoveryResolved(service: MdnsServiceInfo) {
        println("$DEBUG_TAG ${service.ip}")
        if (service.name == ESP_SERVICE_NAME) {
            espUnits = espUnits + listOf(Esp32Unit(service.name, service.ip))
        }
    }
    // ************** Networking ***********************
}