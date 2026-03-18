package com.example.docvault.data.repository

import com.example.docvault.data.local.dao.*
import com.example.docvault.data.local.entity.*
import com.example.docvault.domain.model.*
import com.example.docvault.domain.repository.*

class DocumentRepositoryImpl(
    private val documentDao: DocumentDao,
    private val documentAccessDao: DocumentAccessDao
) : DocumentRepository {

    private fun DocumentEntity.toDomain(): Document = Document(
        id = id.toString(),
        name = name,
        type = type ?: DocumentType.PDF,
        path = path,
        lastAccess = lastAccess ?: 0L
    )

    private fun Document.toEntity(): DocumentEntity = DocumentEntity(
        id = id?.toIntOrNull() ?: 0,
        name = name,
        type = type,
        path = path,
        lastAccess = lastAccess
    )

    private fun DocumentAccessEntity.toDomain(): DocumentAccess = DocumentAccess(
        documentId = documentId.toString(),
        timestamp = timestamp,
        action = action,
        locationAddress = locationAddress
    )

    private fun DocumentAccess.toEntity(): DocumentAccessEntity = DocumentAccessEntity(
        id = 0,
        documentId = documentId?.toIntOrNull() ?: 0,
        timestamp = timestamp,
        action = action,
        locationAddress = locationAddress
    )

    override suspend fun getAllDocuments(): List<Document> =
        documentDao.getAllDocuments().map { it.toDomain() }

    override suspend fun getDocumentById(id: String): Document? {
        val entity = documentDao.getDocumentById(id.toIntOrNull() ?: return null)
        return entity?.toDomain()
    }

    override suspend fun insertDocument(document: Document) {
        documentDao.insertDocument(document.toEntity())
    }

    override suspend fun deleteDocument(id: String) {
        val entity = documentDao.getDocumentById(id.toIntOrNull() ?: return)
        entity?.let {
            documentDao.deleteDocument(it)
        }
    }

    override suspend fun insertAccessLog(access: DocumentAccess) {
        documentAccessDao.insertAccess(access.toEntity())
    }

    override suspend fun getAccessLogs(documentId: String): List<DocumentAccess> {
        return documentAccessDao.getAccesses(documentId.toIntOrNull() ?: 0)
            .map { it.toDomain() }
    }

    override suspend fun updateLastAccess(documentId: String) {
        val entity = documentDao.getDocumentById(documentId.toIntOrNull() ?: return) ?: return
        val updated = entity.copy(lastAccess = System.currentTimeMillis())
        documentDao.insertDocument(updated)
    }
}