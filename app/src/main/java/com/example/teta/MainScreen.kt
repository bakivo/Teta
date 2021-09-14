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
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
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

    Column {
        content()
    }
}

@ExperimentalGraphicsApi
@Composable
fun ModeSelection(modes: List<String> = emptyList(), onModeSelected: (Int) -> Unit)
{
    Box(
        modifier = Modifier
            .fillMaxSize(),
        Alignment.Center)
    {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly)
        {
            // Rainbow
            ButtonWithAnimatedBackground(
                onClicked = { onModeSelected(LedModes.RUNNING_COLOR.ordinal) },
                modifier = modeButtonModifier(),
                animatedBackground = { AnimatedRainbowBackground() }) { MyText(LedModes.RUNNING_COLOR.s) }

            // Rotation
            ButtonWithAnimatedBackground(
                onClicked = { onModeSelected(LedModes.ROTATING_COLOR.ordinal) },
                modifier = modeButtonModifier(),
                animatedBackground = { AnimatedFluidBackground() }) { MyText(LedModes.ROTATING_COLOR.s) }

            // Rotation
            ButtonWithAnimatedBackground(
                onClicked = { onModeSelected(LedModes.SWITCHING_COLOR.ordinal) },
                modifier = modeButtonModifier(),
                animatedBackground = { AnimatedSwitchedColorsBackground() }) { MyText(LedModes.SWITCHING_COLOR.s) }

            // Solid Color selection
            Button(onClick = { onModeSelected(LedModes.SOLID_COLOR.ordinal) }, modifier = modeButtonModifier()) {
                MyText(text = LedModes.SOLID_COLOR.s)
            }
        }
    }
}
val modeButtonModifier = fun ColumnScope.() = Modifier
    .fillMaxWidth()
    .padding(5.dp)
    .weight(1f)

@Composable
fun MyText(text: String) {
    Text(
        text = text,
        fontSize = TextUnit.Unspecified,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold)
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
                    onClick = { onNodeSelected(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    Text(
                        text = esp32Unit.name + " : " + esp32Unit.ip)
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

@Composable
fun SelectionModePreview(){
    TetaTheme {
        Surface(color = MaterialTheme.colors.background) {
            //ModeSelection(modes = LedModes.values().toList().map { it.s }, onModeSelected = {})
        }
    }
}