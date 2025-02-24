package com.example.speedreader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.speedreader.viewmodel.SpeedReaderViewModel
import com.example.speedreader.data.model.QuizQuestion
@Composable
fun QuizDialog(viewModel: SpeedReaderViewModel) {
    val question = viewModel.quizQuestion.value ?: return
    AlertDialog(
        onDismissRequest = {},
        title = { Text(question.question) },
        text = {
            Column {
                question.options.forEachIndexed { index, option ->
                    Button(
                        onClick = { viewModel.submitQuizAnswer(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {}
    )
}