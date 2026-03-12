package com.example.docvault.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docvault.domain.model.DocumentAccess
import com.example.docvault.domain.model.AccessAction
import com.example.docvault.domain.usecase.DocumentUseCase
import com.example.docvault.presentation.states.DocumentDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentDetailViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DocumentDetailUiState>(DocumentDetailUiState.Loading)
    val uiState: StateFlow<DocumentDetailUiState> = _uiState.asStateFlow()

    fun loadDocument(id: String) {
        _uiState.value = DocumentDetailUiState.Loading
        viewModelScope.launch {
            try {
                val document = documentUseCase.getDocumentById(id)
                _uiState.value = if (document == null) {
                    DocumentDetailUiState.Error("Document not found")
                } else {
                    DocumentDetailUiState.RequireBiometric
                }
            } catch (e: Exception) {
                _uiState.value = DocumentDetailUiState.Error(e.message)
            }
        }
    }

    fun onBiometricSuccess(documentId: String, locationAddress: String? = null) {
        viewModelScope.launch {
            val document = documentUseCase.getDocumentById(documentId)
            document?.let {
                documentUseCase.updateLastAccess(documentId)

                val access = DocumentAccess(
                    documentId = documentId,
                    timestamp = System.currentTimeMillis(),
                    action = AccessAction.VIEWED,
                    locationAddress = locationAddress
                )
                documentUseCase.insertAccessLog(access)

                val accessLogs = documentUseCase.getAccessLogs(documentId)
                _uiState.value = DocumentDetailUiState.Success(
                    document = it,
                    accessLogs = accessLogs
                )
            }
        }
    }

    fun deleteDocument(documentId: String, locationAddress: String? = null) {
        viewModelScope.launch {
            documentUseCase.deleteDocument(documentId)

            val access = DocumentAccess(
                documentId = documentId,
                timestamp = System.currentTimeMillis(),
                action = AccessAction.DELETED,
                locationAddress = locationAddress
            )
            documentUseCase.insertAccessLog(access)

            _uiState.value = DocumentDetailUiState.Deleted
        }
    }
}