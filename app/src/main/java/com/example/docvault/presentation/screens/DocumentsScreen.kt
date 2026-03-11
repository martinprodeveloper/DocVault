package com.example.docvault.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.docvault.presentation.states.DocumentsUiState
import com.example.docvault.presentation.viewmodels.DocumentsViewModel

@Composable
fun DocumentsScreen(
    onDocumentClick: (String) -> Unit,
    viewModel: DocumentsViewModel = hiltViewModel()
) {

    val state = viewModel.uiState

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        when (state) {

            is DocumentsUiState.Loading -> {
                CircularProgressIndicator()
            }

            is DocumentsUiState.Empty -> {
                Text("NO DOCUMENTS")
            }

            is DocumentsUiState.Error -> {
                Text("ERROR: ${state.message}")
            }

            is DocumentsUiState.Success -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {

                    items(state.documents) { document ->

                        Text(
                            text = document.name ?: "DOCUMENT",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    document.id?.let {
                                        onDocumentClick(it)
                                    }
                                }
                                .padding(16.dp)
                        )

                    }

                }

            }

            is DocumentsUiState.Idle -> {}

        }

    }
}