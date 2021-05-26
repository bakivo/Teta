package com.example.teta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}

@Composable
private fun MainActivityScreen(viewModel: TetaViewModel) {
    MainScreen(
        viewModel.color,
        viewModel.hue,
        viewModel.saturation,
        viewModel.lightness,
        viewModel::onHueChange,
        viewModel::onSaturationChange,
        viewModel::onLightnessChange,
        onColorChanged = viewModel::sendNewColor)
}

/*
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TetaTheme {
        Greeting("Android")
    }
}*/
