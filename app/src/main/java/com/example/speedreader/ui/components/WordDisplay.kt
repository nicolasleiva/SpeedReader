package com.example.speedreader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.speedreader.viewmodel.SpeedReaderViewModel

@Composable
fun WordDisplay(viewModel: SpeedReaderViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = viewModel.words.value.getOrElse(viewModel.currentWordIndex.value) { "Carga un archivo" },
            style = MaterialTheme.typography.headlineLarge
        )
    }
}