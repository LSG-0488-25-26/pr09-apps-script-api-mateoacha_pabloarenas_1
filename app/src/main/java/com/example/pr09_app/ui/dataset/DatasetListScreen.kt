package com.example.pr09_app.ui.dataset

import com.example.pr09_app.BuildConfig
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pr09_app.data.model.WorldCup

/**
 * Pantalla principal con listado de mundiales y formulario de inserción
 * Implementa dos tabs: Ver Lista y Agregar Registro
 */
@Composable
fun DatasetListScreen(
    viewModel: DatasetViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.observeAsState(DatasetUiState())
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ==================== HEADER ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "FIFA World Cup",
                    style = MaterialTheme.typography.headlineSmall
                )
                // Ayuda para depurar si estamos apuntando al deployment correcto de la Web App.
                // No mostramos toda la URL para evitar exponer datos sensibles.
                val scriptId = BuildConfig.BASE_URL
                    .substringAfter("/macros/s/")
                    .substringBefore("/")
                Text(
                    text = "Web App id: ${scriptId.take(8)}…",
                    style = MaterialTheme.typography.bodySmall,
                )
                Button(onClick = onLogout) {
                    Text("Salir")
                }
            }

            // ==================== TABS ====================
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Ver Datos") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Agregar") }
                )
            }

            // ==================== TAB CONTENTS ====================
            when (selectedTabIndex) {
                0 -> DatasetListTab(viewModel, state)
                1 -> DatasetInsertTab(viewModel, state)
            }
        }
    }
}

/**
 * Tab 1: Listado de mundiales con filtro por país
 */
@Composable
private fun DatasetListTab(
    viewModel: DatasetViewModel,
    state: DatasetUiState,
    modifier: Modifier = Modifier
) {
    var countryFilter by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filtro por país
        OutlinedTextField(
            value = countryFilter,
            onValueChange = { countryFilter = it },
            label = { Text("Filtrar por país") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.loadWorldCups(null) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ver Todos")
            }
            Button(
                onClick = { 
                    if (countryFilter.isNotBlank()) {
                        viewModel.loadWorldCups(countryFilter)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Buscar")
            }
        }

        // Estado de carga
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            return@Column
        }

        // Mostrar error
        state.error?.let {
            Text(
                text = "Error: $it",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            return@Column
        }

        // Sin datos
        if (state.worldCups.isEmpty()) {
            Text(
                text = "No hay datos disponibles. Presiona 'Ver Todos' para cargar.",
                style = MaterialTheme.typography.bodyMedium
            )
            return@Column
        }

        // Lista de mundiales
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.worldCups) { worldCup ->
                WorldCupCard(worldCup)
            }
        }
    }
}

/**
 * Tab 2: Formulario para insertar nuevo mundial
 */
@Composable
private fun DatasetInsertTab(
    viewModel: DatasetViewModel,
    state: DatasetUiState,
    modifier: Modifier = Modifier
) {
    var yearInput by remember { mutableStateOf("") }
    var countryInput by remember { mutableStateOf("") }
    var winnerInput by remember { mutableStateOf("") }
    var runnerfupInput by remember { mutableStateOf("") }
    var thirdInput by remember { mutableStateOf("") }
    var fourthInput by remember { mutableStateOf("") }
    var goalsInput by remember { mutableStateOf("") }
    var qualifiedInput by remember { mutableStateOf("") }
    var matchesInput by remember { mutableStateOf("") }
    var attendanceInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Nuevo Registro de Mundial",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Mostrar error
        item {
            state.insertError?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Mostrar éxito
        item {
            state.insertSuccess?.let {
                Text(
                    text = "✓ $it",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Campos del formulario
        item {
            OutlinedTextField(
                value = yearInput,
                onValueChange = { yearInput = it },
                label = { Text("Año *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = countryInput,
                onValueChange = { countryInput = it },
                label = { Text("País Anfitrión *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = winnerInput,
                onValueChange = { winnerInput = it },
                label = { Text("Ganador *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = runnerfupInput,
                onValueChange = { runnerfupInput = it },
                label = { Text("Subcampeón") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = thirdInput,
                onValueChange = { thirdInput = it },
                label = { Text("Tercer Lugar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = fourthInput,
                onValueChange = { fourthInput = it },
                label = { Text("Cuarto Lugar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = goalsInput,
                onValueChange = { goalsInput = it },
                label = { Text("Goles Totales") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = qualifiedInput,
                onValueChange = { qualifiedInput = it },
                label = { Text("Equipos Calificados") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = matchesInput,
                onValueChange = { matchesInput = it },
                label = { Text("Partidos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = attendanceInput,
                onValueChange = { attendanceInput = it },
                label = { Text("Asistencia") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Botón insertar
        item {
            Button(
                onClick = {
                    if (yearInput.isBlank() || countryInput.isBlank() || winnerInput.isBlank()) {
                        return@Button
                    }

                    val data: Map<String, Any> = mapOf(
                        "year" to (yearInput.toIntOrNull() ?: 0),
                        "country" to countryInput,
                        "winner" to winnerInput,
                        "runnerup" to runnerfupInput,
                        "third" to thirdInput,
                        "fourth" to fourthInput,
                        "goals" to (goalsInput.toIntOrNull() ?: 0),
                        "qualified" to (qualifiedInput.toIntOrNull() ?: 0),
                        "matches" to (matchesInput.toIntOrNull() ?: 0),
                        "attendance" to attendanceInput
                    )

                    viewModel.insertWorldCup(data)

                    // Limpiar formulario
                    yearInput = ""
                    countryInput = ""
                    winnerInput = ""
                    runnerfupInput = ""
                    thirdInput = ""
                    fourthInput = ""
                    goalsInput = ""
                    qualifiedInput = ""
                    matchesInput = ""
                    attendanceInput = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = !state.insertLoading
            ) {
                if (state.insertLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                } else {
                    Text("Insertar Registro")
                }
            }
        }
    }
}

/**
 * Card para mostrar un mundial
 */
@Composable
private fun WorldCupCard(worldCup: WorldCup) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${worldCup.year} - ${worldCup.country}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "🏆 Ganador: ${worldCup.winner}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "🥈 Subcampeón: ${worldCup.runnerup}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "🥉 3°: ${worldCup.third} | 4°: ${worldCup.fourth}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "⚽ ${worldCup.matches} partidos | ${worldCup.goals} goles",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "👥 ${worldCup.qualified} equipos",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

