package com.example.speedreader.data.model

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int
)