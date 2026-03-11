package com.example.docvault.presentation.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.domain.usecase.DocumentUseCase
import com.example.docvault.presentation.states.DocumentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf<DocumentsUiState>(DocumentsUiState.Idle)
    val uiState: State<DocumentsUiState> = _uiState

    private var allDocuments: List<Document> = emptyList()

    private val _selectedFilter = mutableStateOf<DocumentType?>(null)
    val selectedFilter: State<DocumentType?> = _selectedFilter

    init {
        loadDocuments()
    }

    fun loadDocuments() {
        _uiState.value = DocumentsUiState.Loading

        viewModelScope.launch {
            try {
                allDocuments = documentUseCase.getAllDocuments()

                _uiState.value = if (allDocuments.isEmpty()) {
                    DocumentsUiState.Empty
                } else {
                    DocumentsUiState.Success(
                        documents = allDocuments,
                        selectedFilter = _selectedFilter.value
                    )
                }

            } catch (e: Exception) {
                _uiState.value = DocumentsUiState.Error(e.message)
            }
        }
    }

    fun filterDocuments(type: DocumentType?) {
        _selectedFilter.value = type

        viewModelScope.launch {
            val filtered = type?.let { t ->
                allDocuments.filter { it.type == t }
            } ?: allDocuments

            _uiState.value = if (filtered.isEmpty()) {
                DocumentsUiState.Empty
            } else {
                DocumentsUiState.Success(
                    documents = filtered,
                    selectedFilter = type
                )
            }
        }
    }
}