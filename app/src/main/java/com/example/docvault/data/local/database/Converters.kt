package com.example.docvault.data.local.database

import androidx.room.TypeConverter
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.domain.model.AccessAction

class Converters {
    @TypeConverter
    fun fromDocumentType(type: DocumentType?): String? = type?.name

    @TypeConverter
    fun toDocumentType(value: String?): DocumentType? = value?.let { DocumentType.valueOf(it) }

    @TypeConverter
    fun fromAccessAction(action: AccessAction?): String? = action?.name

    @TypeConverter
    fun toAccessAction(value: String?): AccessAction? = value?.let { AccessAction.valueOf(it) }
}