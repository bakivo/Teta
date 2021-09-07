package com.example.teta

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teta.utils.toColor
import com.example.teta.utils.toRGB
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  Screen with settings for solid color mode
 *  @param backColor a current color based on chosen.
 *  @param
 *  @param onHueChanged a callback for new selected value
 */
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
    /*if (toastMessage.isNotEmpty()) {
        Toast.makeText(LocalContext.current, toastMessage, Toast.LENGTH_LONG).show()
    }*/
    Box(Modifier.fillMaxSize().background(backColor), Alignment.Center) {
        Column(
            Modifier
                .padding(5.dp)
                .width(IntrinsicSize.Max), Arrangement.Center, Alignment.CenterHorizontally)
        {

            Text("${espUnit.name} : ${espUnit.ip}")

            HueCanvas(hue){ onHueChanged(it) }

            Spacer(modifier = Modifier.requiredHeight(10.dp).fillMaxWidth())

            MySliderHolder(false) {
                MySlider(value = saturation, range = SATURATION_RANGE) { onSaturationChanged(it) }
            }

            Spacer(modifier = Modifier.requiredHeight(10.dp))

            MySliderHolder(false) {
                MySlider(value = lightness, range = LIGHTNESS_RANGE) { onlightnessChanged(it) }
            }
            /*MySliderHolder( true,
                {
                    ClickableSliderIcon(icon = TetaIcon.MinSaturation, onClick = {  })
                },
                {
                    ClickableSliderIcon(icon = TetaIcon.MaxSaturation, onClick = {} )
                },
                {
                    MySlider(value = saturation, range = SATURATION_RANGE) { onSaturationChanged(it) }
                }
            )*/

        }
    }
}

@Composable
fun EmptyScreen(content: @Composable ColumnScope.() -> Unit = {}) {
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
    Column() {
        Button(onClick = { }) {
            Canvas(modifier = Modifier
                .requiredHeight(30.dp)
                .fillMaxWidth(), onDraw = {
                val hslArray = FloatArray(3)
                hslArray[1] = 1f
                hslArray[2] = 0.5f
                val hueUnitWidth: Float = this.size.width / HUE_SCALE_UNITS
                val hueUnitSize = Size(hueUnitWidth, this.size.height)
                for (hue in 0 until HUE_SCALE_UNITS) {
                    hslArray[0] = ((shift + hue) % HUE_SCALE_UNITS).toFloat()
                    drawRect(hslArray.toRGB().toColor(), topLeft = Offset(hue * hueUnitWidth, 0f), size = hueUnitSize)
                }
            } )
        }
        // Display a circular progress indicator whilst loading
        CircularProgressIndicator()
        content()
    }
}
@Composable
fun ModeSelection(modes: List<String>, onModeSelected: (Int) -> Unit) {
    Column(Modifier.padding(40.dp)) {
        modes.forEachIndexed { index, mode ->
            Button(
                modifier = Modifier.padding(vertical = 5.dp),
                onClick = { onModeSelected(index) }
            ) {
                Text(
                    text = mode,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@Composable
fun SelectionScreen(nodes: List<Esp32Unit>, onNodeSelected: (Int) -> Unit) {
    Box(Modifier
        .padding(10.dp)
        .fillMaxSize()
        .width(IntrinsicSize.Max)
        .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier
            .padding(20.dp)
            .width(IntrinsicSize.Max)
        ) {
            nodes.forEachIndexed { index, esp32Unit ->
                Button(onClick = { onNodeSelected(index) }, Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
                {
                    Text("${esp32Unit.name} : ${esp32Unit.ip}")
                }
            }
        }
    }
}
@Composable
fun Screen(){
    Box(Modifier
        .padding(10.dp)
        .fillMaxSize()
        .width(IntrinsicSize.Max)
        .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier
            .padding(10.dp)
            .width(IntrinsicSize.Max))
        {
            Button(onClick = { /*TODO*/ }, Modifier.fillMaxWidth()) {
                Text(text = "lolololo", )
            }
            Button(onClick = { /*TODO*/ }, Modifier.fillMaxWidth()) {
                Text(text = "mnmnmnmnmn", )
            }
        }
    }
}
@Composable
fun TestScreen() {
    Column(Modifier
        .padding(10.dp)
        .width(IntrinsicSize.Max))
    {
        Button(onClick = { /*TODO*/ }, Modifier.fillMaxWidth()) {
            Text(text = "lolololo", )
        }
        Button(onClick = { /*TODO*/ }, Modifier.fillMaxWidth()) {
            Text(text = "mnmnmnmnmn", )
        }
    }
}