package com.example.pr09_app.ui.dataset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pr09_app.data.model.WorldCup
import com.example.pr09_app.data.repository.DatasetRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el estado de la lista de mundiales
 * 
 * Responsabilidades:
 * - Ejecutar peticiones a la API (GET, POST)
 * - Mantener estado con LiveData
 * - Manejar errores y loading states
 */
class DatasetViewModel(private val repo: DatasetRepository) : ViewModel() {
    
    // ==================== ESTADOS ====================
    
    private val _uiState = MutableLiveData(DatasetUiState())
    val uiState: LiveData<DatasetUiState> = _uiState

    // ==================== PUBLIC METHODS ====================

    /**
     * Carga la lista de mundiales, opcionalmente filtrada por país
     * @param countryFilter País para filtrar (null = sin filtro)
     */
    fun loadWorldCups(countryFilter: String? = null) {
        _uiState.value = _uiState.value?.copy(
            isLoading = true, 
            error = null
        )
        
        viewModelScope.launch {
            val result = repo.fetchWorldCups(countryFilter)
            _uiState.postValue(
                result.fold(
                    onSuccess = { worldCups ->
                        DatasetUiState(
                            isLoading = false,
                            worldCups = worldCups,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        DatasetUiState(
                            isLoading = false,
                            worldCups = emptyList(),
                            error = exception.message ?: "Error desconocido"
                        )
                    }
                )
            )
        }
    }

    /**
     * Inserta un nuevo registro de mundial y recarga la lista
     * @param worldCupData Map con campos: year, country, winner, runnerup, third, fourth, goals, qualified, matches, attendance
     */
    fun insertWorldCup(worldCupData: Map<String, Any>) {
        _uiState.value = _uiState.value?.copy(
            insertLoading = true,
            insertError = null,
            insertSuccess = null
        )
        
        viewModelScope.launch {
            val result = repo.insertWorldCup(worldCupData)
            result.fold(
                onSuccess = {
                    _uiState.postValue(
                        _uiState.value?.copy(
                            insertLoading = false,
                            insertSuccess = "Registro insertado correctamente",
                            insertError = null
                        )
                    )
                    // Recargar la lista
                    loadWorldCups()
                },
                onFailure = { exception ->
                    _uiState.postValue(
                        _uiState.value?.copy(
                            insertLoading = false,
                            insertSuccess = null,
                            insertError = exception.message ?: "Error al insertar"
                        )
                    )
                }
            )
        }
    }

    /**
     * Limpia el estado de inserción
     */
    fun clearInsertState() {
        _uiState.value = _uiState.value?.copy(
            insertError = null,
            insertSuccess = null
        )
    }
}

/**
 * Estado de la UI para la lista de mundiales
 */
data class DatasetUiState(
    val isLoading: Boolean = false,
    val worldCups: List<WorldCup> = emptyList(),
    val error: String? = null,
    val insertLoading: Boolean = false,
    val insertError: String? = null,
    val insertSuccess: String? = null
)

