package com.example.speedreader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.speedreader.viewmodel.SpeedReaderViewModel

@Composable
fun Controls(viewModel: SpeedReaderViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { viewModel.togglePlayPause() }) {
            Icon(
                imageVector = if (viewModel.isPlaying.value) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (viewModel.isPlaying.value) "Pausar" else "Reproducir"
            )
            Text(if (viewModel.isPlaying.value) "Pausar" else "Reproducir")
        }
        Column {
            Text("Velocidad: ${viewModel.wpm.value} WPM")
            Slider(
                value = viewModel.wpm.value.toFloat(),
                onValueChange = { viewModel.setWpm(it.toInt()) },
                valueRange = 100f..1000f,
                steps = 8
            )
        }
    }
}