package com.example.teta

enum class TetaIcon(val imageVector: Int) {
    MinLightness(R.drawable.ic_baseline_brightness_3_24),
    MaxLightness(R.drawable.ic_baseline_brightness_high_24),
    MinSaturation(R.drawable.ic_baseline_wb_cloudy_24),
    MaxSaturation(R.drawable.ic_round_palette_24)
}
const val HUE_SCALE_UNITS = 360
val HUE_RANGE: ClosedFloatingPointRange<Float> = 0f..360f
val SATURATION_RANGE: ClosedFloatingPointRange<Float> = 0f..1f
val LIGHTNESS_RANGE: ClosedFloatingPointRange<Float> = 0f..1f
