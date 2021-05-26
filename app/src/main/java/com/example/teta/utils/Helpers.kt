package com.example.teta.utils

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils

fun FloatArray.toRGB() = ColorUtils.HSLToColor(this)
fun Int.toColor() = Color(this)


