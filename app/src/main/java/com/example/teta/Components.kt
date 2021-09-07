package com.example.teta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teta.utils.toColor
import com.example.teta.utils.toRGB
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  Canvas element that draws rainbow like representation of the hue range [0.0..360.0]
 *  @param hueValue a current value to show on a canvas as a grey line
 *  @param onHueChanged a callback for new selected value
 */
@Composable
fun HueCanvas(hueValue: Float, onHueChanged: (Float) -> Unit) {
    Surface(Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.primarySurface,
            shape = RoundedCornerShape(8.dp)
    ) {
        Canvas(
                Modifier
                    .requiredHeight(100.dp).requiredWidthIn(360.dp)
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

@Composable
fun SliderBackground(
    modifier: Modifier = Modifier,
    elevate: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier
                .padding(5.dp)
                .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = if (elevate) 2.dp else 0.dp) {
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        content()
    }
}

val Drawing: DrawScope.() -> Unit = {

    val hslArray = FloatArray(3)
    hslArray[1] = 1f
    hslArray[2] = 0.5f
    val hueUnitWidth: Float = this.size.width / HUE_SCALE_UNITS
    val hueUnitSize = Size(hueUnitWidth, this.size.height)
    for (hue in 0..HUE_SCALE_UNITS) {
        hslArray[0] = hue.toFloat()
        this.drawRect(color = hslArray.toRGB().toColor(), topLeft = Offset(hue * hueUnitWidth, 0f), size = hueUnitSize)
    }
}

@Composable
fun FancyButton(onDraw: DrawScope.() -> Unit = {}) {
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
    Canvas(Modifier
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
            drawRect(hslArray.toRGB().toColor(), topLeft = Offset(hue * hueUnitWidth, 0f), size = hueUnitSize)
        }
    }

}


@Composable
fun ClickableSliderIcon(
        icon: TetaIcon,
        onClick: () -> Unit = {},
        onLongClick: () -> Unit = {},
) {
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
    Surface(Modifier.fillMaxWidth(),
        color = MaterialTheme.colors.primarySurface,
        shape = RoundedCornerShape(8.dp)
    ) {
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
@Preview
@Composable
fun Prev1(){
}

@Preview
@Composable
fun PreviewHueCanvas() {
    //HueCanvas(hueValue = 55f) {}
}