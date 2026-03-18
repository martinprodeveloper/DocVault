package com.example.docvault.presentation.states

import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentAccess

sealed interface DocumentDetailUiState {

    data object Loading : DocumentDetailUiState

    data object RequireBiometric : DocumentDetailUiState

    data class Success(
        val document: Document,
        val accessLogs: List<DocumentAccess> = emptyList()
    ) : DocumentDetailUiState

    data object Deleted : DocumentDetailUiState

    data class Error(
        val message: String?
    ) : DocumentDetailUiState
}