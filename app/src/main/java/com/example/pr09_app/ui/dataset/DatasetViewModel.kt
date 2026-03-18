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

    private var lastAction: String = "listAll"

    fun load(action: String) {
        lastAction = action
        // Mantener estado de inserción (éxito/error) para no "borrar" mensajes al refrescar lista.
        _uiState.value = _uiState.value?.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.fetchWorldCups(action)
            _uiState.postValue(
                result.fold(
                    onSuccess = { rows ->
                        DatasetUiState(
                            isLoading = false,
                            worldCups = rows,
                            error = null,
                            insertLoading = _uiState.value?.insertLoading ?: false,
                            insertError = _uiState.value?.insertError,
                            insertSuccess = _uiState.value?.insertSuccess,
                        )
                    },
                    onFailure = { e ->
                        DatasetUiState(
                            isLoading = false,
                            worldCups = emptyList(),
                            error = e.message ?: "Error cargando datos",
                            insertLoading = _uiState.value?.insertLoading ?: false,
                            insertError = _uiState.value?.insertError,
                            insertSuccess = _uiState.value?.insertSuccess,
                        )
                    }
                )
            )
        }
    }

    fun insertWorldCup(body: Map<String, Any>) {
        _uiState.value = _uiState.value?.copy(
            insertLoading = true,
            insertError = null,
            insertSuccess = null,
        )
        viewModelScope.launch {
            val result = repo.insertWorldCup(body)
            result.fold(
                onSuccess = {
                    _uiState.postValue(
                        _uiState.value?.copy(
                            insertLoading = false,
                            insertSuccess = "Insertado correctamente",
                            insertError = null,
                        )
                    )
                    load(lastAction)
                },
                onFailure = { e ->
                    _uiState.postValue(
                        _uiState.value?.copy(
                            insertLoading = false,
                            insertSuccess = null,
                            insertError = e.message ?: "Error insertando datos",
                        )
                    )
                }
            )
        }
    }
}

data class DatasetUiState(
    val isLoading: Boolean = false,
    val worldCups: List<WorldCup> = emptyList(),
    val error: String? = null,
    val insertLoading: Boolean = false,
    val insertError: String? = null,
    val insertSuccess: String? = null,
)

