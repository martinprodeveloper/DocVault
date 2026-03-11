package com.example.docvault.domain.usecase

import com.example.docvault.domain.model.Document
import com.example.docvault.domain.repository.DocumentRepository
import javax.inject.Inject

class DocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {

    suspend fun getAllDocuments(): List<Document> = repository.getAllDocuments()

    suspend fun getDocumentById(id: String): Document? = repository.getDocumentById(id)

    suspend fun insertDocument(document: Document) = repository.insertDocument(document)

    suspend fun deleteDocument(id: String) = repository.deleteDocument(id)
}