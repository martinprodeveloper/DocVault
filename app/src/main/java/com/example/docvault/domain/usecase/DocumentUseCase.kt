package com.example.docvault.domain.usecase

import com.example.docvault.domain.model.*
import com.example.docvault.domain.repository.*
import javax.inject.Inject

class DocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {

    suspend fun getAllDocuments(): List<Document> = repository.getAllDocuments()

    suspend fun getDocumentById(id: String): Document? = repository.getDocumentById(id)

    suspend fun insertDocument(document: Document) = repository.insertDocument(document)

    suspend fun deleteDocument(id: String) = repository.deleteDocument(id)

    suspend fun insertAccessLog(access: DocumentAccess) {
        repository.insertAccessLog(access)
    }

    suspend fun getAccessLogs(documentId: String): List<DocumentAccess> {
        return repository.getAccessLogs(documentId)
    }

    suspend fun updateLastAccess(documentId: String) = repository.updateLastAccess(documentId)
}