package com.example.docvault.presentation.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.presentation.screens.components.DocumentFilter
import com.example.docvault.presentation.states.DocumentsUiState
import com.example.docvault.presentation.viewmodels.DocumentsViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun DocumentsScreen(
    context: Context,
    onDocumentClick: (String) -> Unit,
    viewModel: DocumentsViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.value
    val selectedFilter = viewModel.selectedFilter.value
    val scope = rememberCoroutineScope()
    var showPickerMenu by remember { mutableStateOf(false) }

    val cameraUri = remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                cameraUri.value?.let { uri ->
                    val name = "doc_${System.currentTimeMillis()}"
                    viewModel.addDocument(context, uri, name, DocumentType.IMAGE)
                }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                cameraUri.value?.let { takePictureLauncher.launch(it) }
            } else {
                Toast.makeText(context, "No hay permiso de la cámara", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val pickDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val name = "doc_${System.currentTimeMillis()}"
                val type = if (it.toString().endsWith(".pdf")) DocumentType.PDF else DocumentType.IMAGE
                viewModel.addDocument(context, it, name, type)
            }
        }
    )

    Scaffold(
        floatingActionButton = {
            Box {
                FloatingActionButton(onClick = { showPickerMenu = true }) {
                    Text("+")
                }

                DropdownMenu(
                    expanded = showPickerMenu,
                    onDismissRequest = { showPickerMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Galería") },
                        onClick = {
                            showPickerMenu = false
                            pickDocumentLauncher.launch("*/*")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cámara") },
                        onClick = {
                            showPickerMenu = false
                            val tempFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
                            cameraUri.value = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                tempFile
                            )
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            DocumentFilter(
                selectedFilter = selectedFilter,
                onFilterSelected = { type ->
                    scope.launch { viewModel.filterDocuments(type) }
                }
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    is DocumentsUiState.Loading -> CircularProgressIndicator()
                    is DocumentsUiState.Empty -> Text("NO DOCUMENTS")
                    is DocumentsUiState.Error -> Text("ERROR: ${state.message}")
                    is DocumentsUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.documents) { document ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { document.id?.let { onDocumentClick(it) } },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = document.name ?: "DOCUMENT",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = document.type?.name ?: "-",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is DocumentsUiState.Idle -> {}
                }
            }

        }

    }
}