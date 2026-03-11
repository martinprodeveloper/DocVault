package com.example.docvault.domain.repository

import com.example.docvault.domain.model.Document

interface DocumentRepository {
    suspend fun getAllDocuments(): List<Document>
    suspend fun getDocumentById(id: String): Document?
    suspend fun insertDocument(document: Document)
    suspend fun deleteDocument(id: String)
}