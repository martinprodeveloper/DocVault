package com.example.docvault.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docvault.domain.usecase.DocumentUseCase
import com.example.docvault.presentation.states.DocumentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase
) : ViewModel() {

    var uiState by mutableStateOf<DocumentsUiState>(DocumentsUiState.Idle)
        private set

    init {
        loadDocuments()
    }

    fun loadDocuments() {

        uiState = DocumentsUiState.Loading

        viewModelScope.launch {

            try {

                val documents = documentUseCase.getAllDocuments()

                uiState =
                    if (documents.isEmpty()) {
                        DocumentsUiState.Empty
                    } else {
                        DocumentsUiState.Success(documents)
                    }

            } catch (e: Exception) {

                uiState = DocumentsUiState.Error(e.message)

            }

        }
    }
}