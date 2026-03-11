package com.example.docvault.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docvault.domain.usecase.DocumentUseCase
import com.example.docvault.presentation.states.DocumentDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentDetailViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase
) : ViewModel() {

    var uiState by mutableStateOf<DocumentDetailUiState>(
        DocumentDetailUiState.Loading
    )
        private set

    fun loadDocument(id: String) {

        uiState = DocumentDetailUiState.Loading

        viewModelScope.launch {

            try {

                val document = documentUseCase.getDocumentById(id)

                if (document == null) {

                    uiState = DocumentDetailUiState.Error("Document not found")

                } else {

                    uiState = DocumentDetailUiState.RequireBiometric

                }

            } catch (e: Exception) {

                uiState = DocumentDetailUiState.Error(e.message)

            }

        }
    }

    fun onBiometricSuccess(documentId: String) {

        viewModelScope.launch {

            val document = documentUseCase.getDocumentById(documentId)

            document?.let {

                uiState = DocumentDetailUiState.Success(
                    document = it
                )

            }

        }
    }

    fun deleteDocument(documentId: String) {

        viewModelScope.launch {

            documentUseCase.deleteDocument(documentId)

            uiState = DocumentDetailUiState.Deleted

        }
    }
}