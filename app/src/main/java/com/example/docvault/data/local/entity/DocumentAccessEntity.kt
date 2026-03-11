package com.example.docvault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.docvault.domain.model.AccessAction

@Entity(tableName = "document_access")
data class DocumentAccessEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val documentId: Int,
    val timestamp: Long? = null,
    val action: AccessAction? = null
)