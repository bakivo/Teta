package com.example.teta.utils

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils

fun FloatArray.HSL2RGB() = ColorUtils.HSLToColor(this)
fun Int.toColor() = Color(this)
