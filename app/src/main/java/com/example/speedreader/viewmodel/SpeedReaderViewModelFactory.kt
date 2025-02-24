package com.example.speedreader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.speedreader.data.repository.FileRepository
import com.example.speedreader.data.repository.GroqRepository

class SpeedReaderViewModelFactory(
    private val fileRepository: FileRepository,
    private val groqRepository: GroqRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpeedReaderViewModel::class.java)) {
            return SpeedReaderViewModel(fileRepository, groqRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}