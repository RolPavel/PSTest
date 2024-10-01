package com.rolstudio.pstest.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rolstudio.pstest.repository.SearchRepository

class MainViewModelProviderFactory(
    val app: Application,
    private val searchRepository: SearchRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(app, searchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}