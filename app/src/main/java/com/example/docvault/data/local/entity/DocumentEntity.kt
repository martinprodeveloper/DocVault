package com.example.docvault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.docvault.domain.model.DocumentType

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String? = null,
    val type: DocumentType? = null,
    val path: String? = null,
    val lastAccess: Long? = null
)