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
import com.example.teta.utils.await2
import com.example.teta.utils.await
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.lang.Exception

const val ESP_SERVICE_NAME = "ESP32"
const val NOT_SELECTED = -1
data class Esp32Unit(val name: String, val ip: String)
private val client = OkHttpClient()

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
        println("$DEBUG_TAG ${service.ip}")
        if (service.name.contains(ESP_SERVICE_NAME)) {
            espUnits = espUnits + listOf(Esp32Unit(service.name, service.ip))
        }
    }
    // ************** Networking ***********************
    private suspend fun fetchHeartbeatInfo(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()
        val response = client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    println(response.body!!.string())
                    println(response.message)
                }
            }

        })
    }

    private suspend fun sendHSV(ip: String, path: String) {
        val postBody = """
            {
            	"red": ${(color.red*100).toInt()},
            	"green": ${(color.green*100).toInt()},
            	"blue": ${(color.blue*100).toInt()}
            }
        """.trimIndent().also { println(DEBUG_TAG + it) }
        val request = Request.Builder()
            .url("http:/$ip/$path")
            .addHeader("Content-Type", "application/json")
            .post(postBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        val response = client.newCall(request).await()

        /*CoroutineScope(IO).launch {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                println(response.body!!.string())
            }
        }*/
        withContext(IO) {
            response.body?.use {
                println(it.charStream().readText())
            }
        }

    }
    // *************************************************
}
