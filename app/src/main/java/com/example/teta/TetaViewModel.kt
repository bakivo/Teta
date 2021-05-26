package com.example.teta

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.AndroidViewModel

class TetaViewModel(application: Application) : AndroidViewModel(application) {

    var color: Color by mutableStateOf(Color.Magenta)
        private set
    var hue: Float by mutableStateOf(0f)
        private set
    var saturation: Float by mutableStateOf(0f)
        private set
    var lightness: Float by mutableStateOf(0f)
        private set
    private val hslArray = FloatArray(3)

    private fun setup() {
        hue = 180f; saturation = 0.7f; lightness = 0.45f
        color = updateColor()
    }
    private fun updateColor(): Color {
        return Color(ColorUtils.HSLToColor(hslArray.apply { this[0] = hue; this[1] = saturation ; this[2] = lightness }))
    }
    init {
        setup()
    }
    fun sendNewColor(color: Color) {
        println("$DEBUG_TAG${color.alpha} : ${color.value} : ${color.green} : ${color.blue}")
        this.color = color
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
}