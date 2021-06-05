package com.example.teta

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teta.ui.theme.TetaTheme

@Composable
fun SliderIcon(
    icon: TetaIcon,
    modifier: Modifier = Modifier
) {
    val painter = painterResource(id = icon.imageVector)
    val paintWidth = painter.intrinsicSize.width.dp
    Icon(
        painter = painterResource(id = icon.imageVector),
        modifier = modifier.requiredWidth(24.dp).padding(3.dp),
        contentDescription = "",
    )
}

@Composable
fun TetaSlider(
    value: Float,
    modifier: Modifier = Modifier,
    onValueChanged: (Float) -> Unit
) {

    Slider(
        modifier = modifier.fillMaxWidth(),
        value = value,
        steps = 100,
        valueRange = 0.0f..1.0f,
        onValueChange = { onValueChanged(it) }
    )
}

@Composable
fun SliderBackground(
    modifier: Modifier = Modifier,
    elevate: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier.padding(5.dp),
        //color = Color.White,
        shape = RoundedCornerShape(4.dp),
        elevation = if (elevate) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Preview
@Composable
fun Prev1(){
    TetaTheme(false) {
        SliderBackground(elevate = true) {
            SliderIcon(icon = TetaIcon.MaxLightness, modifier = Modifier)
            TetaSlider(value = 0.45f, modifier = Modifier.fillMaxWidth().weight(1f, false)) {}
            SliderIcon(icon = TetaIcon.MinLightness, modifier = Modifier)
        }
    }
}