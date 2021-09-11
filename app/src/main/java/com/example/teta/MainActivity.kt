package com.example.teta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.teta.ui.theme.TetaTheme

const val DEBUG_TAG = "TESTS: "

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

@Composable
private fun MainActivityScreen(viewModel: TetaViewModel) {

    when (viewModel.screenHierarchy) {

        ScreenHierarchy.EMPTY -> EmptyScreen()

        ScreenHierarchy.NODE_SELECTION -> SelectionScreen(nodes = viewModel.espUnits, onNodeSelected = viewModel::onNodeSelected)

        ScreenHierarchy.MODE_SELECTION -> ModeSelection(modes = LedModes.values().toList().map { it.s }, onModeSelected = viewModel::onModeSelected)

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

                LedModes.RUNNING_COLOR -> EmptyScreen {
                    FancyButton()
                }

                LedModes.ROTATING_COLOR -> {
                    TODO()
                }

                LedModes.SWITCHING_COLOR -> {
                    TODO()
                }
            }
    }
}

@Preview
@Composable
fun Test1(){
    TetaTheme {
        Surface(
                color = MaterialTheme.colors.background,
        ) {
        }
    }
}
@Preview
@Composable
fun Test2(){
    TetaTheme {
        Surface(
                color = MaterialTheme.colors.background,
        ) {
        }
    }
}