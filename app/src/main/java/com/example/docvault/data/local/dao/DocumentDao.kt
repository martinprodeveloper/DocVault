package com.example.docvault.data.local.dao

import androidx.room.*
import com.example.docvault.data.local.entity.DocumentEntity

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents")
    suspend fun getAllDocuments(): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Int): DocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)

    @Query("SELECT * FROM documents WHERE type = :type")
    suspend fun getDocumentsByType(type: String): List<DocumentEntity>
}