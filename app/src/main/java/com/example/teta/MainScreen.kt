package com.example.teta

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teta.utils.toColor
import com.example.teta.utils.toRGB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

const val DEBUG_TAG = "TESTS: "
const val HUE_SCALE_UNITS = 360
/**
 *  Modifier extension function for HueCanvas
 **/
fun Modifier.fancy() = this
    .padding(horizontal = 15.dp, vertical = 5.dp)
    .requiredHeight(100.dp)
    .fillMaxWidth()

@Composable
fun MainScreen(
    backColor: Color,
    hue: Float,
    saturation: Float,
    lightness: Float,
    onHueChanged: (Float) -> Unit,
    onSaturationChanged: (Float) -> Unit,
    onlightnessChanged: (Float) -> Unit,
    espUnit: Esp32Unit = Esp32Unit("Test", "127.0.0.1"),
    toastMessage: String = ""
) {
    Toast.makeText(LocalContext.current,toastMessage, Toast.LENGTH_LONG).show()
    Box(
        Modifier
            .background(backColor)
            .fillMaxSize()) {
        Column {
            Text(text = "${espUnit.name} : ${espUnit.ip}", modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp), textAlign = TextAlign.Center)

            HueCanvas(hueValue = hue, onHueChanged = onHueChanged)

            SliderBackground() {
                SliderIcon(icon = TetaIcon.MinSaturation)
                TetaSlider(value = saturation, modifier = Modifier.weight(1f)) { onSaturationChanged(it) }
                SliderIcon(icon = TetaIcon.MaxSaturation)
            }

            SliderBackground() {
                SliderIcon(icon = TetaIcon.MinLightness)
                TetaSlider(value = lightness, modifier = Modifier.weight(1f)) { onlightnessChanged(it) }
                SliderIcon(icon = TetaIcon.MaxLightness)
            }
        }
    }
}
/**
 *  Canvas element that draws rainbow like representation of the hue range [0.0..360.0]
 *  @param hueValue a current value to show on a canvas as a grey line
 *  @param onHueChanged a callback for new selected value
 */
@Composable
fun HueCanvas(hueValue: Float, onHueChanged: (Float) -> Unit) {
    Surface(
        color = MaterialTheme.colors.primarySurface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Canvas(
            Modifier
                .fancy()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            onHueChanged((HUE_SCALE_UNITS * tapOffset.x / this.size.width))
                        },
                    )
                },
        )
        {
            val hueUnitWidth: Float = this.size.width / HUE_SCALE_UNITS
            val hueUnitSize = Size(hueUnitWidth, this.size.height)
            val hslArray = FloatArray(3)
            hslArray[1] = 1f    // saturation value for hue with maximum saturation
            hslArray[2] = 0.5f  // lightness value for hue without tint or shade

            for (hue in 0..HUE_SCALE_UNITS){
                hslArray[0] = hue.toFloat()
                drawRect(
                    color = hslArray.toRGB().toColor(),
                    topLeft = Offset(hue * hueUnitWidth,0f),
                    size = hueUnitSize
                )
            }
            // highlight a current hue
            drawCircle(
                color = hslArray.apply { this[0] = hueValue }.toRGB().toColor(),
                radius = 10f,
                center = Offset(hueValue * hueUnitWidth, 0f)
            )
            drawCircle(
                color = hslArray.apply { this[0] = hueValue }.toRGB().toColor(),
                radius = 10f,
                center = Offset(hueValue * hueUnitWidth, size.height)
            )
        }
    }
}

@Preview
@Composable
fun PreviewHueCanvas() {
    HueCanvas(hueValue = 55f) {}
}

