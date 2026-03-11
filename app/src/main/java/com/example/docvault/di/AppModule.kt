package com.example.docvault.di

import android.content.Context
import androidx.room.Room
import com.example.docvault.data.local.dao.DocumentDao
import com.example.docvault.data.local.dao.DocumentAccessDao
import com.example.docvault.data.local.database.DocVaultDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DocVaultDatabase {
        return Room.databaseBuilder(
            context,
            DocVaultDatabase::class.java,
            "docvault_db"
        ).build()
    }

    @Provides
    fun provideDocumentDao(db: DocVaultDatabase): DocumentDao = db.documentDao()

    @Provides
    fun provideDocumentAccessDao(db: DocVaultDatabase): DocumentAccessDao = db.documentAccessDao()
}