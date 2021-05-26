package com.example.teta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.teta.utils.toColor
import com.example.teta.utils.toRGB

const val DEBUG_TAG = "TESTS: "
const val HUE_SCALE_UNITS = 360
enum class HSL {
    HUE,
    SATURATION,
    LIGHTNESS
}
fun Modifier.fancy() = this
    .padding(30.dp)
    .requiredHeight(300.dp)
    .fillMaxWidth()
fun Modifier.hueCanvasTapListener(onTapAction: (Int) -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(
        onTap = {
            onTapAction((HUE_SCALE_UNITS * it.x / size.width).toInt())
        }
    )
}

@Composable
fun MainScreen(
    backColor: Color,
    hue: Float,
    saturation: Float,
    lightness: Float,
    onHueChanged: (Float) -> Unit,
    onSaturationChanged: (Float) -> Unit,
    onlightnessChanged: (Float) -> Unit,
    onColorChanged: (Color) -> Unit = {}
) {
    val hslArray = FloatArray(3)

    Box(modifier = Modifier.background(backColor)) {
        Column(Modifier.fillMaxSize()) {
            HueCanvas(hueValue = hue, onHueChanged = onHueChanged)
            Slider(
                value = saturation,
                steps = 100,
                valueRange = 0.0f..1.0f,
                onValueChange = { onSaturationChanged(it) }
            )
            Slider(
                value = lightness,
                steps = 100,
                valueRange = 0.0f..1.0f,
                onValueChange = { onlightnessChanged(it) }
            )
        }
    }
}

@Composable
fun HueCanvas(
    hueValue: Float,
    onHueChanged: (Float) -> Unit,
) {
    Canvas(modifier = Modifier.fancy().pointerInput(Unit) {
            detectTapGestures(
                onTap = { onHueChanged((HUE_SCALE_UNITS * it.x / size.width))}
            )
        }
    )
    {
        val hueUnitWidth = size.width / HUE_SCALE_UNITS
        val hsvArray = FloatArray(3)
        hsvArray[1] = 1f
        hsvArray[2] = 0.5f
        val size = Size(hueUnitWidth, size.height)

        for (i in 0..HUE_SCALE_UNITS){
            hsvArray[0] = i.toFloat()
            drawRect(
                color = hsvArray.toRGB().toColor(),
                topLeft = Offset(i * hueUnitWidth,0f),
                size = size
            )
        }
        drawRect(Color.DarkGray, Offset(hueValue * hueUnitWidth,0f), size)
    }

}

@Composable
fun SaturationSlider(saturation: Float, onSaturationChanged: (Float) -> Unit) {
    Slider(
        value = saturation,
        steps = 100,
        valueRange = 0.0f..1.0f,
        onValueChange = { println("$DEBUG_TAG: saturation $it"); onSaturationChanged(it) }
    )
}
@Composable
fun LightnessSlider(lightness: Float, onlightnessChanged: (Float) -> Unit) {
    Slider(
        value = lightness,
        steps = 100,
        valueRange = 0.0f..1.0f,
        onValueChange = { println("$DEBUG_TAG: lightness $it"); onlightnessChanged(it) }
    )
}
