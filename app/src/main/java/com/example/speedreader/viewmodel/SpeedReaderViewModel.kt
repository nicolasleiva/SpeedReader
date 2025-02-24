package com.example.speedreader.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedreader.data.repository.FileRepository
import com.example.speedreader.data.repository.GroqRepository
import com.example.speedreader.data.model.QuizQuestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SpeedReaderViewModel(
    private val fileRepository: FileRepository,
    private val groqRepository: GroqRepository
) : ViewModel() {
    var words = mutableStateOf<List<String>>(emptyList())
    var currentWordIndex = mutableStateOf(0)
    var isPlaying = mutableStateOf(false)
    var wpm = mutableStateOf(300)
    var progress = mutableStateOf(0f)
    var estimatedTime = mutableStateOf("")
    var remainingTime = mutableStateOf("")
    var showQuiz = mutableStateOf(false)
    var quizQuestion = mutableStateOf<QuizQuestion?>(null)
    private var timerJob: Job? = null
    private val quizInterval = 200
    private var lastQuizIndex = 0

    fun loadFile(uri: Uri) {
        val wordList = fileRepository.readTextFromUri(uri)
        words.value = wordList
        currentWordIndex.value = 0
        lastQuizIndex = 0
        calculateEstimatedTime()
    }

    fun togglePlayPause() {
        isPlaying.value = !isPlaying.value
        if (isPlaying.value) startTimer() else stopTimer()
    }

    fun setWpm(newWpm: Int) {
        wpm.value = newWpm
        calculateEstimatedTime()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isPlaying.value && currentWordIndex.value < words.value.size) {
                delay(TimeUnit.MINUTES.toMillis(1) / wpm.value)
                if (!isPlaying.value) break

                val nextIndex = currentWordIndex.value + 1
                currentWordIndex.value = nextIndex
                progress.value = (nextIndex.toFloat() / words.value.size) * 100
                updateRemainingTime()

                if (nextIndex - lastQuizIndex >= quizInterval) {
                    pauseAndShowQuiz(nextIndex)
                    break
                }
            }
            if (currentWordIndex.value >= words.value.size) isPlaying.value = false
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun calculateEstimatedTime() {
        val minutes = words.value.size / wpm.value
        estimatedTime.value = "$minutes minute${if (minutes != 1) "s" else ""}"
    }

    private fun updateRemainingTime() {
        val remainingWords = words.value.size - currentWordIndex.value
        val minutes = remainingWords / wpm.value
        remainingTime.value = "$minutes minute${if (minutes != 1) "s" else ""}"
    }

    private suspend fun pauseAndShowQuiz(index: Int) {
        isPlaying.value = false
        stopTimer()
        showQuiz.value = true
        lastQuizIndex = index
        val contextStart = maxOf(0, index - 49)
        val context = words.value.subList(contextStart, index).joinToString(" ")
        quizQuestion.value = groqRepository.generateQuizQuestion(context)
    }

    fun submitQuizAnswer(selectedIndex: Int) {
        val correct = selectedIndex == quizQuestion.value?.correctAnswer
        if (correct) {
            showQuiz.value = false
            quizQuestion.value = null
            isPlaying.value = true
            startTimer()
        }
    }
}