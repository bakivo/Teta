package com.example.teta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.teta.utils.toColor
import com.example.teta.utils.HSL2RGB
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  Canvas element that draws rainbow like representation of the hue range [0.0..360.0]
 *  @param hueValue a current value to show on a canvas as a grey line
 *  @param onHueChanged a callback for new selected value
 */
@Composable
fun HuePicker(hueValue: Float, onHueChanged: (Float) -> Unit) {
        Canvas(
            Modifier
                .requiredHeight(100.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { Offset ->
                            onHueChanged((HUE_SCALE_UNITS * Offset.x / this.size.width))
                        },
                    )
                },
        )
        {

            val hueUnitWidth: Float = this.size.width / HUE_SCALE_UNITS
            val hueUnitSize = Size(hueUnitWidth, this.size.height)

            val hslArray = FloatArray(3)
            hslArray[1] = 1f    // maximum saturation
            hslArray[2] = 0.5f  // lightness without tint or shade
            // draw main canvas with all possible hue values
            for (hue in 0..HUE_SCALE_UNITS){
                hslArray[0] = hue.toFloat()
                drawRect(
                        color = hslArray.HSL2RGB().toColor(),
                        topLeft = Offset(hue * hueUnitWidth,0f),
                        size = hueUnitSize
                )
            }
            // draw to circles to highlight the current hue
            drawCircle(
                    color = hslArray.apply { this[0] = hueValue }.HSL2RGB().toColor(),
                    radius = 15f,
                    center = Offset(hueValue * hueUnitWidth, 0f)
            )
            drawCircle(
                    color = hslArray.apply { this[0] = hueValue }.HSL2RGB().toColor(),
                    radius = 15f,
                    center = Offset(hueValue * hueUnitWidth, size.height)
            )
        }
}


@Composable
fun FancyButton()
{
    var shift: Int by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        launch {
            while (true) {
                delay(30)
                if (shift == (HUE_SCALE_UNITS - 1) ) shift = 0
                shift += 1
            }
        }
    }
    Canvas(
        Modifier
            .fillMaxWidth()
            .requiredHeight(300.dp)
    ) {
        val hslArray = FloatArray(3)
        hslArray[1] = 1f
        hslArray[2] = 0.5f
        val hueUnitWidth: Float = this.size.width / HUE_SCALE_UNITS
        val hueUnitSize = Size(hueUnitWidth, this.size.height)
        for (hue in 0 until HUE_SCALE_UNITS) {
            hslArray[0] = ((shift + hue) % HUE_SCALE_UNITS).toFloat()
            drawRect(hslArray.HSL2RGB().toColor(), topLeft = Offset(hue * hueUnitWidth, 0f), size = hueUnitSize)
        }
    }
}

@Composable
fun AnimatedRainbowBackground(
    speedMillis: Long = 30)
{
    var shift: Int by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        launch {
            while (true) {
                delay(speedMillis)
                if (shift == (HUE_SCALE_UNITS - 1) ) shift = 0
                shift += 1
            }
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.4f)
    ) {
        val hslArray = FloatArray(3)
        hslArray[1] = 1f
        hslArray[2] = 0.5f

        val hueUnitWidth: Float = this.size.width / HUE_SCALE_UNITS
        val hueUnitSize = Size(hueUnitWidth, this.size.height)

        for (hue in 0 until HUE_SCALE_UNITS) {
            hslArray[0] = ((shift + hue) % HUE_SCALE_UNITS).toFloat()

            drawRect(
                color = hslArray
                    .HSL2RGB()
                    .toColor(),
                topLeft = Offset(hue * hueUnitWidth, 0f),
                size = hueUnitSize
            )
        }
    }
}

@Composable
fun AnimatedFluidBackground(
    speedMillis: Long = 30)
{
    var hueValue: Int by remember { mutableStateOf(0)}

    LaunchedEffect(Unit) {
        while (true) {
            hueValue = if (hueValue == 360) 0 else hueValue + 1
            delay(speedMillis)
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.4f)
    ) {
        val hsl = floatArrayOf( hueValue.toFloat(), 1f, 0.5f)
        drawRect(
            color = hsl.HSL2RGB().toColor(),
            topLeft = Offset.Zero,
            size = Size(size.width, size.height)
        )
    }
}

@Composable
fun ButtonWithAnimatedBackground(
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
    animatedBackground: @Composable () -> Unit,
    text: @Composable () -> Unit)
{
    Button(
        onClick = { onClicked() },
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            text()
            animatedBackground()
        }
    }
}

@Composable
fun ClickableSliderIcon(
        icon: TetaIcon,
        onClick: () -> Unit = {},
        onLongClick: () -> Unit = {})
{
    Icon(painter = painterResource(id = icon.imageVector),
            contentDescription = "Icon",
            modifier = Modifier
                .requiredSize(30.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onLongClick() },
                        onTap = { onClick() }
                    )
                }
    )
}

/**
 *
 */
@Composable
fun MySlider(value: Float, range: ClosedFloatingPointRange<Float>, onValueChanged: (Float) -> Unit) {

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(16.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    onValueChanged(it.x / (size.width / (range.endInclusive - range.start)) + range.start)
                }
            }
        ) {
            val unitWidth = size.width / (range.endInclusive - range.start)

            val position = when {
                value < range.start -> 0f
                value > range.endInclusive -> size.width
                else -> (value - range.start) * unitWidth
            }
            val color1 = Color(224, 222, 223)
            val color2 = Color(247, 245, 246)
            drawRoundRect(color1, Offset.Zero, Size(position, size.height), CornerRadius(15f, 15f))
            drawRoundRect(
                color2,
                Offset(position, 0f),
                Size(size.width - position, size.height),
                CornerRadius(15f, 15f)
            )
        }

}

@Composable
fun MySliderHolder(
        iconsEnabled: Boolean,
        leftSlot: @Composable BoxScope.() -> Unit = {},
        rightSlot: @Composable BoxScope.() -> Unit = {},
        centreSlot: @Composable BoxScope.() -> Unit
) {
    Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {

        if (iconsEnabled) {
            Box(Modifier.weight(1f), Alignment.Center) { leftSlot() }
        }

        Box(Modifier.weight(11f, true), Alignment.Center) { centreSlot() }

        if (iconsEnabled){
            Box(Modifier.weight(1f), Alignment.Center) { rightSlot() }
        }
    }
}

val RainbowDrawing: DrawScope.() -> Unit = {
    val hslArray = FloatArray(3)
    hslArray[1] = 1f
    hslArray[2] = 0.5f
    val hueUnitWidth: Float = this.size.width / HUE_SCALE_UNITS
    val hueUnitSize = Size(hueUnitWidth, this.size.height)
    for (hue in 0..HUE_SCALE_UNITS) {
        hslArray[0] = hue.toFloat()
        this.drawRect(color = hslArray.HSL2RGB().toColor(), topLeft = Offset(hue * hueUnitWidth, 0f), size = hueUnitSize)
    }
}
