package com.example.speedreader.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speedreader.data.repository.FileRepository
import com.example.speedreader.data.repository.GroqRepository
import com.example.speedreader.ui.components.Controls
import com.example.speedreader.ui.components.QuizDialog
import com.example.speedreader.ui.components.WordDisplay
import com.example.speedreader.ui.theme.SpeedReaderTheme
import com.example.speedreader.viewmodel.SpeedReaderViewModel
import com.example.speedreader.viewmodel.SpeedReaderViewModelFactory

class MainActivity : ComponentActivity() {

    // Registro para seleccionar un archivo (actualmente, el callback no realiza ninguna acción)
    private val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Aquí podrías invocar, por ejemplo, viewModel.loadFile(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedReaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpeedReaderScreen(filePicker::launch)
                }
            }
        }
    }
}

@Composable
fun SpeedReaderScreen(
    onFilePick: (String) -> Unit,
    viewModel: SpeedReaderViewModel = viewModel(
        factory = SpeedReaderViewModelFactory(
            FileRepository(LocalContext.current),
            GroqRepository()
        )
    )
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { onFilePick("text/plain") }) {
            Text("Cargar archivo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        WordDisplay(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Controls(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Progreso: ${viewModel.progress.value.toInt()}%")
        Text("Tiempo estimado: ${viewModel.estimatedTime.value}")
        Text("Tiempo restante: ${viewModel.remainingTime.value}")

        if (viewModel.showQuiz.value) {
            QuizDialog(viewModel)
        }
    }
}
