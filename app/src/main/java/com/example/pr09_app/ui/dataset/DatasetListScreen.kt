package com.example.pr09_app.ui.dataset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DatasetListScreen(
    viewModel: DatasetViewModel,
    type: String,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.observeAsState(DatasetUiState())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Datos (GET)", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = onLogout) { Text("Salir") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.load(type) }) {
                Text("Cargar")
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(state.rows) { index, row ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Fila ${index + 1}", style = MaterialTheme.typography.titleMedium)
                        row.entries.take(6).forEach { (k, v) ->
                            Text(text = "$k: $v", style = MaterialTheme.typography.bodySmall)
                        }
                        if (row.size > 6) {
                            Text(text = "...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

