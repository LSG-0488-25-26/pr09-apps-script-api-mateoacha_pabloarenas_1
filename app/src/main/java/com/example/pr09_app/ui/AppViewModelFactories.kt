package com.example.pr09_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pr09_app.data.auth.AuthStore
import com.example.pr09_app.data.repository.DatasetRepository
import com.example.pr09_app.ui.auth.AuthViewModel
import com.example.pr09_app.ui.dataset.DatasetViewModel

class AuthViewModelFactory(
    private val store: AuthStore,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(store) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DatasetViewModelFactory(
    private val repo: DatasetRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatasetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatasetViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

