package com.example.docvault.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.domain.usecase.DocumentUseCase
import com.example.docvault.presentation.states.DocumentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DocumentsUiState>(DocumentsUiState.Idle)
    val uiState: StateFlow<DocumentsUiState> = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow<DocumentType?>(null)
    val selectedFilter: StateFlow<DocumentType?> = _selectedFilter.asStateFlow()

    private var allDocuments: List<Document> = emptyList()

    init { loadDocuments() }

    fun loadDocuments() {
        _uiState.value = DocumentsUiState.Loading
        viewModelScope.launch {
            try {
                allDocuments = documentUseCase.getAllDocuments()
                updateUiState()
            } catch (e: Exception) {
                _uiState.value = DocumentsUiState.Error(e.message)
            }
        }
    }

    fun filterDocuments(type: DocumentType?) {
        _selectedFilter.value = type
        updateUiState()
    }

    private fun updateUiState() {
        val filtered = _selectedFilter.value?.let { t ->
            allDocuments.filter { it.type == t }
        } ?: allDocuments

        _uiState.value = when {
            filtered.isEmpty() -> DocumentsUiState.Empty
            else -> DocumentsUiState.Success(filtered, _selectedFilter.value)
        }
    }

    fun addDocument(context: Context, uri: Uri, name: String, type: DocumentType) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, name)
                encryptFile(context, uri, file)

                val document = Document(
                    id = System.currentTimeMillis().toString(),
                    name = name,
                    type = type,
                    path = file.absolutePath,
                    lastAccess = System.currentTimeMillis()
                )

                documentUseCase.insertDocument(document)

                allDocuments = allDocuments + document
                updateUiState()
            } catch (e: Exception) {
                _uiState.value = DocumentsUiState.Error(e.message)
            }
        }
    }

    private fun encryptFile(context: Context, sourceUri: Uri, destFile: File) {
        val keyBytes = ByteArray(32)
        val keySpec = SecretKeySpec(keyBytes, "AES")
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destFile.outputStream().use { fos ->
                CipherOutputStream(fos, cipher).use { cos ->
                    input.copyTo(cos)
                }
            }
        }
    }
}