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

@Composable
private fun AppRoot() {
    val settingsRepository = SettingsRepository(
        nomFitxer = "users",
        context = androidx.compose.ui.platform.LocalContext.current
    )
    val store = AuthStore(settingsRepository)
    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(store))

    val repo = DatasetRepository(ApiClient.service)
    val datasetVm: DatasetViewModel = viewModel(factory = DatasetViewModelFactory(repo))

    val authState by authVm.uiState.observeAsState(AuthUiState())

    LaunchedEffect(Unit) {
        authVm.refreshSession()
    }

    if (!authState.isLoggedIn) {
        AuthScreen(viewModel = authVm)
    } else {
        // Endpoint de ejemplo: ajustadlo a vuestros 3 endpoints reales del Apps Script
        DatasetListScreen(
            viewModel = datasetVm,
            endpoint = "sheet1",
            onLogout = authVm::logout,
        )
    }
}