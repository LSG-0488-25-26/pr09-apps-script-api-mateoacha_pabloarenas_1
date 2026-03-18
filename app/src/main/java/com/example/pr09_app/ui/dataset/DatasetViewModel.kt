package com.example.pr09_app.ui.dataset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pr09_app.data.repository.DatasetRepository
import com.example.pr09_app.data.model.WorldCup
import kotlinx.coroutines.launch

class DatasetViewModel(private val repo: DatasetRepository) : ViewModel() {
    private val _uiState = MutableLiveData(DatasetUiState())
    val uiState: LiveData<DatasetUiState> = _uiState

    fun load(action: String) {
        _uiState.value = _uiState.value?.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.fetchWorldCups(action)
            _uiState.postValue(
                result.fold(
                    onSuccess = { rows ->
                        DatasetUiState(
                            isLoading = false,
                            worldCups = rows,
                            error = null
                        )
                    },
                    onFailure = { e ->
                        DatasetUiState(
                            isLoading = false,
                            worldCups = emptyList(),
                            error = e.message ?: "Error cargando datos"
                        )
                    }
                )
            )
        }
    }
}

data class DatasetUiState(
    val isLoading: Boolean = false,
    val worldCups: List<WorldCup> = emptyList(),
    val error: String? = null,
)

