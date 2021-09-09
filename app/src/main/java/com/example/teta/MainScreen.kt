package com.example.teta

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.teta.ui.theme.TetaTheme
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
    if (toastMessage.isNotEmpty()) {
        Toast.makeText(LocalContext.current, toastMessage, Toast.LENGTH_LONG).show()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(backColor), Alignment.Center) {
        Surface(Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.primarySurface,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                Modifier
                    .padding(5.dp)
                    .width(IntrinsicSize.Max), Arrangement.Center, Alignment.CenterHorizontally
            )
            {

                Text("${espUnit.name} : ${espUnit.ip}")

                HuePicker(hue) { onHueChanged(it) }

                Spacer(modifier = Modifier
                    .requiredHeight(10.dp)
                    .fillMaxWidth())

                MySliderHolder(false) {
                    MySlider(
                        value = saturation,
                        range = SATURATION_RANGE
                    ) { onSaturationChanged(it) }
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
    Column {
        FancyButton()
        // Display a circular progress indicator whilst loading
        CircularProgressIndicator()
        content()
    }
}

@Composable
fun ModeSelection(modes: List<String>, onModeSelected: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), Alignment.Center,) {
        Column(
            Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(30.dp)
                .width(IntrinsicSize.Max), verticalArrangement = Arrangement.SpaceEvenly) {

            modes.forEachIndexed { index, mode -> when {

                mode == LedModes.RUNNING_COLOR.s -> NewButton(
                    text = mode, modifier = Modifier.fillMaxWidth(), onClicked = { onModeSelected(index) })
                else -> Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onModeSelected(index) }
                ) {
                    Text(
                        text = mode,
                        modifier = Modifier,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            }
        }
    }
}

@Composable
fun SelectionModePreview(){
    TetaTheme {
        Surface(color = MaterialTheme.colors.background) {

        }
    }
}

@Composable
fun SelectionScreen(nodes: List<Esp32Unit>, onNodeSelected: (Int) -> Unit) {
    Box(
        Modifier
            .padding(5.dp)
            .fillMaxSize(),
        Alignment.Center,
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(5.dp)
                .width(IntrinsicSize.Max)) {
            nodes.forEachIndexed { index, esp32Unit ->
                Button(
                    { onNodeSelected(index) },
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    Text(text = esp32Unit.name + " : " + esp32Unit.ip)
                }
            }
        }
    }
}

@Composable
fun SelectionScreenPreview(){
    fun generateUnits(n: Int) = mutableListOf<Esp32Unit>().also {
        (1..n).forEach { i ->
            it.add(Esp32Unit("unit $i", "192.168.1.$i"))
        }
    }
    TetaTheme {
        Surface(color = MaterialTheme.colors.background) {
            SelectionScreen(nodes = generateUnits(4), onNodeSelected = {})
        }
    }
}