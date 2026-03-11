package com.example.docvault.presentation.states

import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentType

sealed interface DocumentsUiState {

    data object Idle : DocumentsUiState

    data object Loading : DocumentsUiState

    data class Success(
        val documents: List<Document>,
        val selectedFilter: DocumentType? = null
    ) : DocumentsUiState

    data object Empty : DocumentsUiState

    data class Error(
        val message: String?
    ) : DocumentsUiState
}