package com.example.docvault.fake

import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentAccess
import com.example.docvault.domain.repository.DocumentRepository

class FakeDocumentRepository : DocumentRepository {

    private val documents = mutableListOf<Document>()
    private val accessLogs = mutableListOf<DocumentAccess>()

    override suspend fun getAllDocuments(): List<Document> {
        return documents
    }

    override suspend fun getDocumentById(id: String): Document? {
        return documents.find { it.id == id }
    }

    override suspend fun insertDocument(document: Document) {
        documents.add(document)
    }

    override suspend fun deleteDocument(id: String) {
        documents.removeIf { it.id == id }
    }

    override suspend fun insertAccessLog(access: DocumentAccess) {
        accessLogs.add(access)
    }

    override suspend fun getAccessLogs(documentId: String): List<DocumentAccess> {
        return accessLogs.filter { it.documentId == documentId }
    }

    override suspend fun updateLastAccess(documentId: String) {
        val index = documents.indexOfFirst { it.id == documentId }
        if (index != -1) {
            val doc = documents[index]
            documents[index] = doc.copy(lastAccess = System.currentTimeMillis())
        }
    }
}