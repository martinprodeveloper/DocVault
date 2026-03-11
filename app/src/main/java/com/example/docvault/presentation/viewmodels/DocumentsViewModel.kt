package com.example.docvault.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.domain.usecase.DocumentUseCase
import com.example.docvault.presentation.states.DocumentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _uiState = mutableStateOf<DocumentsUiState>(DocumentsUiState.Idle)
    val uiState: State<DocumentsUiState> = _uiState

    private val _selectedFilter = mutableStateOf<DocumentType?>(null)
    val selectedFilter: State<DocumentType?> = _selectedFilter

    private var allDocuments: List<Document> = emptyList()

    init {
        loadDocuments()
    }

    fun loadDocuments() {
        _uiState.value = DocumentsUiState.Loading
        viewModelScope.launch {
            try {
                // Traer documentos de la DB
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
                // Guardar archivo en storage local y encriptar
                val file = File(context.filesDir, name)
                encryptFile(context, uri, file)

                // Crear modelo de documento
                val document = Document(
                    id = System.currentTimeMillis().toString(),
                    name = name,
                    type = type,
                    path = file.absolutePath,
                    lastAccess = null
                )

                // Guardar en base de datos
                documentUseCase.insertDocument(document)

                // Actualizar lista local y UI
                allDocuments = allDocuments + document
                updateUiState()

            } catch (e: Exception) {
                _uiState.value = DocumentsUiState.Error(e.message)
            }
        }
    }

    private fun encryptFile(context: Context, sourceUri: Uri, destFile: File) {
        val keyBytes = ByteArray(32) // Puedes generar una clave real aquí
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