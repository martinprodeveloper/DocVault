package com.example.docvault.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.docvault.presentation.states.DocumentDetailUiState
import com.example.docvault.presentation.viewmodels.DocumentDetailViewModel

@Composable
fun DocumentDetailScreen(
    documentId: String,
    onBack: () -> Unit,
    viewModel: DocumentDetailViewModel = hiltViewModel()
) {

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    val state = viewModel.uiState

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "DOCUMENT DETAIL",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            when (state) {

                is DocumentDetailUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is DocumentDetailUiState.RequireBiometric -> {
                    Text("BIOMETRIC AUTH REQUIRED")
                }

                is DocumentDetailUiState.Success -> {

                    val document = state.document

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = document.name ?: "DOCUMENT",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("ID: ${document.id}")

                    }

                }

                is DocumentDetailUiState.Deleted -> {
                    Text("DOCUMENT DELETED")
                }

                is DocumentDetailUiState.Error -> {
                    Text("ERROR: ${state.message}")
                }

            }

        }

    }
}