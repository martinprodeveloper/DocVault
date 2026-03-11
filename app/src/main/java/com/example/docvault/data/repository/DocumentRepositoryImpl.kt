package com.example.docvault.data.repository

import com.example.docvault.data.local.dao.DocumentDao
import com.example.docvault.data.local.dao.DocumentAccessDao
import com.example.docvault.data.local.entity.DocumentEntity
import com.example.docvault.data.local.entity.DocumentAccessEntity
import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.domain.model.AccessAction
import com.example.docvault.domain.repository.DocumentRepository

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
            val access = DocumentAccessEntity(
                documentId = it.id,
                timestamp = System.currentTimeMillis(),
                action = AccessAction.DELETED
            )
            documentAccessDao.insertAccess(access)
        }
    }
}