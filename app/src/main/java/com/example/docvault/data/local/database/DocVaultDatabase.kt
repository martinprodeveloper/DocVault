package com.example.docvault.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.docvault.data.local.dao.DocumentDao
import com.example.docvault.data.local.dao.DocumentAccessDao
import com.example.docvault.data.local.entity.DocumentEntity
import com.example.docvault.data.local.entity.DocumentAccessEntity

@Database(
    entities = [DocumentEntity::class, DocumentAccessEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DocVaultDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun documentAccessDao(): DocumentAccessDao
}