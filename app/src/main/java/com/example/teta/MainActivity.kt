package com.example.teta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.unit.dp
import com.example.teta.ui.theme.TetaTheme

const val DEBUG_TAG = "TESTS: "

@ExperimentalGraphicsApi
class MainActivity : ComponentActivity() {
    private val tetaViewModel by viewModels<TetaViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TetaTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainActivityScreen(viewModel = tetaViewModel)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        tetaViewModel.startServiceDiscovery()
    }

    override fun onPause() {
        super.onPause()
        tetaViewModel.stopServiceDiscovery()
    }

    override fun onBackPressed() {
        if (tetaViewModel.shouldExitOnBackPressed()) {
            super.onBackPressed()
        }
    }
}

@ExperimentalGraphicsApi
@Composable
private fun MainActivityScreen(viewModel: TetaViewModel) {

    when (viewModel.screenHierarchy) {

        ScreenHierarchy.EMPTY -> EmptyScreen()

        ScreenHierarchy.NODE_SELECTION -> SelectionScreen(nodes = viewModel.espUnits, onNodeSelected = viewModel::onNodeSelected)

        ScreenHierarchy.MODE_SELECTION -> ModeSelection(onModeSelected = viewModel::onModeSelected)

        ScreenHierarchy.NODE_CONTROL -> when(viewModel.currentMode) {

                LedModes.SOLID_COLOR -> MainScreen(
                        viewModel.color,
                        viewModel.hue,
                        viewModel.saturation,
                        viewModel.lightness,
                        viewModel::onHueChange,
                        viewModel::onSaturationChange,
                        viewModel::onLightnessChange,
                        viewModel.espUnits.get(viewModel.currentPosition),
                        viewModel.toastMessage)

                LedModes.RUNNING_COLOR ->
                    Screen(viewModel.toastMessage) {
                        MyText("Speed")
                        Spacer(Modifier.requiredHeight(10.dp))
                        MySliderHolder(false) {
                            MySlider(viewModel.speedMillis, range = SPEED_MILLIS_RANGE, viewModel::onSpeedChange)
                        }
                    }


                LedModes.ROTATING_COLOR -> {

                }

                LedModes.SWITCHING_COLOR -> {

                }
            }
    }
}

@Composable
fun Test1(){
    TetaTheme {
        Surface(
                color = MaterialTheme.colors.background,
        ) {
        }
    }
}
