package com.example.pr09_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pr09_app.data.auth.AuthStore
import com.example.pr09_app.data.auth.SettingsRepository
import com.example.pr09_app.data.network.ApiClient
import com.example.pr09_app.data.repository.DatasetRepository
import com.example.pr09_app.ui.AuthViewModelFactory
import com.example.pr09_app.ui.DatasetViewModelFactory
import com.example.pr09_app.ui.auth.AuthScreen
import com.example.pr09_app.ui.auth.AuthUiState
import com.example.pr09_app.ui.auth.AuthViewModel
import com.example.pr09_app.ui.dataset.DatasetListScreen
import com.example.pr09_app.ui.dataset.DatasetViewModel
import com.example.pr09_app.ui.theme.PR09appTheme

/**
 * MainActivity - Punto de entrada de la aplicación
 * 
 * Gestiona:
 * - Inicialización de ViewModels
 * - Navegación entre Auth y Dataset screens
 * - Inyección de dependencias
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PR09appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    AppRoot()
                }
            }
        }
    }
}

/**
 * Raíz de la aplicación - Controla la lógica de navegación
 */
@Composable
private fun AppRoot() {
    // ==================== INICIALIZACIÓN DE DEPENDENCIAS ====================
    
    val settingsRepository = SettingsRepository(
        nomFitxer = "AppSettings",
        context = androidx.compose.ui.platform.LocalContext.current
    )
    val store = AuthStore(settingsRepository)
    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(store))

    val repo = DatasetRepository(ApiClient.service)
    val datasetVm: DatasetViewModel = viewModel(factory = DatasetViewModelFactory(repo))

    val authState by authVm.uiState.observeAsState(AuthUiState())

    // ==================== CICLO DE VIDA ====================
    
    LaunchedEffect(Unit) {
        authVm.refreshSession()
    }

    // ==================== NAVEGACIÓN ====================
    
    if (!authState.isLoggedIn) {
        // Mostrar pantalla de Login
        AuthScreen(viewModel = authVm)
    } else {
        // Mostrar pantalla principal de Datos
        DatasetListScreen(
            viewModel = datasetVm,
            onLogout = {
                authVm.logout()
                datasetVm.clearInsertState()
            }
        )
    }
}