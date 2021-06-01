package com.example.teta

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.teta.ui.theme.TetaTheme

class MainActivity : ComponentActivity() {
    private val tetaViewModel by viewModels<TetaViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TetaTheme {
                // A surface container using the 'background' color from the theme
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
}

@Composable
private fun MainActivityScreen(viewModel: TetaViewModel) {
    if (viewModel.espUnits.isEmpty()){
        Text(text = "Empty", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    } else if (viewModel.currentPosition != NOT_SELECTED) {
        MainScreen(
            viewModel.color,
            viewModel.hue,
            viewModel.saturation,
            viewModel.lightness,
            viewModel::onHueChange,
            viewModel::onSaturationChange,
            viewModel::onLightnessChange,
            viewModel.espUnits.get(viewModel.currentPosition)
        )
    } else {
        //
        Column() {
            viewModel.espUnits.forEachIndexed { index, unit ->
                Button(onClick = { viewModel.currentPosition = index }) {
                    Text(text = "${unit.ip} : ${unit.name}", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TetaTheme {
        Greeting("Android")
    }
}*/
