package com.example.app19_jc_neon_bd_remoto.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_neon_bd_remoto.model.Persona
import com.example.app19_jc_neon_bd_remoto.viewmodel.PersonaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPersona(viewModel: PersonaViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarCalendario by remember { mutableStateOf(false) }
    var personaAEliminar by remember { mutableStateOf<Persona?>(null) }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personas") },
                actions = {
                    IconButton(onClick = viewModel::cargarPersonas) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FormularioPersona(
                    state = state,
                    onNombresChange = viewModel::onNombresChange,
                    onApellidosChange = viewModel::onApellidosChange,
                    onDniChange = viewModel::onDniChange,
                    onAbrirCalendario = { mostrarCalendario = true },
                    onGuardar = viewModel::guardar,
                    onCancelar = viewModel::limpiarFormulario
                )
            }

            item {
                Text(
                    text = "Registros (${state.personas.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (state.cargando) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            } else if (state.personas.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay personas registradas.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.personas, key = { it.id }) { persona ->
                    PersonaItem(
                        persona = persona,
                        onEditar = { viewModel.seleccionarParaEditar(persona) },
                        onEliminar = { personaAEliminar = persona }
                    )
                }
            }
        }
    }

    if (mostrarCalendario) {
        SelectorFecha(
            fechaIso = state.fechaNacimiento,
            onFechaSeleccionada = viewModel::onFechaNacimientoChange,
            onCerrar = { mostrarCalendario = false }
        )
    }

    personaAEliminar?.let { persona ->
        AlertDialog(
            onDismissRequest = { personaAEliminar = null },
            title = { Text("Eliminar persona") },
            text = { Text("¿Eliminar a ${persona.nombres} ${persona.apellidos} (DNI ${persona.dni})?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(persona)
                    personaAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { personaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}
