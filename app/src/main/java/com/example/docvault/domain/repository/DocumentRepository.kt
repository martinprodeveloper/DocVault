package com.example.docvault.domain.repository

import com.example.docvault.domain.model.*

interface DocumentRepository {
    suspend fun getAllDocuments(): List<Document>
    suspend fun getDocumentById(id: String): Document?
    suspend fun insertDocument(document: Document)
    suspend fun deleteDocument(id: String)
    suspend fun insertAccessLog(access: DocumentAccess)
    suspend fun getAccessLogs(documentId: String): List<DocumentAccess>
    suspend fun updateLastAccess(documentId: String)
}