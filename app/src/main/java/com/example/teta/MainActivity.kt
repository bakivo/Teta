package com.example.teta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teta.ui.theme.TetaTheme

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

        ScreenHierarchy.EMPTY ->
            EmptyScreen()

        ScreenHierarchy.NODE_SELECTION ->
            SelectionScreen(nodes = viewModel.espUnits, onNodeSelected = viewModel::onNodeSelected)

        ScreenHierarchy.MODE_SELECTION ->
            ModeSelection(modes = LedModes.values().toList().map { it.s }, onModeSelected = viewModel::onModeSelected)

        ScreenHierarchy.NODE_CONTROL ->
            if (viewModel.currentMode == LedModes.SOLID_COLOR) {
                MainScreen(
                    viewModel.color,
                    viewModel.hue,
                    viewModel.saturation,
                    viewModel.lightness,
                    viewModel::onHueChange,
                    viewModel::onSaturationChange,
                    viewModel::onLightnessChange,
                    viewModel.espUnits.get(viewModel.currentPosition),
                    viewModel.toastMessage
                )
            } else {
                EmptyScreen()
            }

    }
}

@Composable
fun EmptyScreen() {
    Text(text = "Empty", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}
@Composable
fun ModeSelection(modes: List<String>, onModeSelected: (Int) -> Unit) {
    Column() {
        modes.forEachIndexed { index, mode ->
            Button(onClick = { onModeSelected(index) }) {
                Text(text = mode)
            }
        }

    }
}
@Composable
fun SelectionScreen(nodes: List<Esp32Unit>, onNodeSelected: (Int) -> Unit) {
    Column(Modifier.padding(20.dp)) {
        nodes.forEachIndexed { index, esp32Unit ->
            Button(
                modifier = Modifier.padding(vertical = 5.dp),
                onClick = { onNodeSelected(index) }
            ) {
                Text(
                    text ="${esp32Unit.name} : ${esp32Unit.ip}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
            }
        }
    }
}
@Preview
@Composable
fun TestModeSelectionScreen(){

    TetaTheme {
        Surface(color = MaterialTheme.colors.background) {
        }
    }
}