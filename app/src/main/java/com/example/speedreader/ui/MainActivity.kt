package com.example.speedreader.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // Registro para seleccionar un archivo; en el callback puedes llamar a viewModel.loadFile(uri)
    private val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Ejemplo: viewModel.loadFile(uri)
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
                    SpeedReaderScaffold(filePicker::launch)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedReaderScaffold(onFilePick: (String) -> Unit) {
    // Instancia del ViewModel usando el Factory correspondiente
    val viewModel: SpeedReaderViewModel = viewModel(
        factory = SpeedReaderViewModelFactory(
            FileRepository(LocalContext.current),
            GroqRepository()
        )
    )
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "SpeedReader",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onFilePick("text/plain") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "Cargar", fontSize = 14.sp)
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Card que enmarca el contenido principal con un look moderno
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Animación para mostrar la palabra actual (usamos el parámetro currentIndex para evitar warning)
                        Crossfade(targetState = viewModel.currentWordIndex.value) { currentWordIndex ->
                            // Dummy use to avoid unused parameter warning
                            currentWordIndex.hashCode()
                            WordDisplay(viewModel)
                        }

                        // Controles para reproducir/pausar y ajustar la velocidad
                        Controls(viewModel)
                        // Indicador de progreso lineal usando lambda para progress
                        LinearProgressIndicator(
                            progress = { viewModel.progress.value / 100f }, // Usar lambda en lugar de un Float directo
                            modifier = Modifier
                                .fillMaxSize()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Estadísticas de tiempo
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "Tiempo estimado: ${viewModel.estimatedTime.value}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Tiempo restante: ${viewModel.remainingTime.value}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                // Muestra el porcentaje de progreso numérico
                Text(
                    text = "Progreso: ${viewModel.progress.value.toInt()}%",
                    style = MaterialTheme.typography.bodyLarge
                )
                // Diálogo para el quiz si se activa
                if (viewModel.showQuiz.value) {
                    QuizDialog(viewModel)
                }
            }
        }
    )
}
