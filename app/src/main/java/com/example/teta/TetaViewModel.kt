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
import com.example.teta.utils.await
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

const val ESP_SERVICE_NAME = "ESP32"
const val NOT_SELECTED = -1
data class Esp32Unit(val name: String, val ip: String)
private val client = OkHttpClient()
enum class ScreenHierarchy {
    EMPTY, NODE_SELECTION, NODE_CONTROL
}
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

    var screenHierarchy by mutableStateOf(ScreenHierarchy.EMPTY)
        private set

    private val hslArray = FloatArray(3)
    private var mdnsDiscovery: ServiceDiscovery
    var toastMessage: String by mutableStateOf("")

    init {
        setup()
        val nsdManager = application.applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
        mdnsDiscovery = ServiceDiscovery(nsdManager = nsdManager, onServiceAddedIP = this::onServiceDiscoveryResolved)
        // following block only for test purpose ---------------------------
        viewModelScope.launch {
            repeat(3) {
                espUnits = espUnits + listOf(Esp32Unit("esp", "$it"))
                delay(1000)
            }
            screenHierarchy = ScreenHierarchy.NODE_SELECTION
        }
        // end-------------------------------------------------------------
    }
    // ***************** GUI **********************************************
    private fun setup() {
        hue = 180f; saturation = 1f; lightness = 0.5f
        color = updateColor()
    }
    fun onNodeSelected(index: Int) {
        currentPosition = index
        screenHierarchy = ScreenHierarchy.NODE_CONTROL
    }
    fun shouldExitOnBackPressed(): Boolean {
        if (screenHierarchy == ScreenHierarchy.NODE_CONTROL) {
            screenHierarchy = ScreenHierarchy.NODE_SELECTION
            return false
        }
        return true
    }
    private fun updateColor(): Color {
        return Color(ColorUtils.HSLToColor(hslArray.apply { this[0] = hue; this[1] = saturation ; this[2] = lightness }))
    }

    fun onHueChange(hue: Float) {
        this.hue = hue
        color = updateColor()
        sendColor()
    }
    fun onSaturationChange(saturation: Float) {
        this.saturation = saturation
        color = updateColor()
        sendColor()
    }
    fun onLightnessChange(lightness: Float) {
        this.lightness = lightness
        color = updateColor()
        sendColor()
    }

    fun sendColor() {
        viewModelScope.launch {
            delay(1000)
            runCatching {
                sendHSV(currentUnit!!.ip,"rgb")
            }.onFailure {
                println(DEBUG_TAG + it.message )
                toastMessage = it.message.toString()
                delay(3000)
                screenHierarchy = ScreenHierarchy.NODE_SELECTION
            }
        }
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
        if (service.name.contains(ESP_SERVICE_NAME)) {
            if (!espUnits.any { it.ip == service.ip }) {
                espUnits = espUnits + listOf(Esp32Unit(service.name, service.ip))
                screenHierarchy = ScreenHierarchy.NODE_SELECTION
            }
        }
    }

    // ************** Networking ***********************
    private suspend fun sendHSV(ip: String, path: String) {
        val postBody = """
            {
            	"red": ${(color.red*100).toInt()},
            	"green": ${(color.green*100).toInt()},
            	"blue": ${(color.blue*100).toInt()}
            }
        """.trimIndent()
        val request = Request.Builder()
            .url("http:/$ip/$path")
            .addHeader("Content-Type", "application/json")
            .post(postBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        val response = client.newCall(request).await()

        withContext(IO) {
            response.body?.use {
                println(it.charStream().readText())
            }
        }
    }
}
