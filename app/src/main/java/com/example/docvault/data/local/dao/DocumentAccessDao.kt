package com.example.docvault.data.local.dao

import androidx.room.*
import com.example.docvault.data.local.entity.DocumentAccessEntity

@Dao
interface DocumentAccessDao {

    @Query("SELECT * FROM document_access WHERE documentId = :docId")
    suspend fun getAccesses(docId: Int): List<DocumentAccessEntity>

    @Insert
    suspend fun insertAccess(access: DocumentAccessEntity)
}