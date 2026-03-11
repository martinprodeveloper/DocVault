package com.example.docvault.di

import com.example.docvault.domain.repository.DocumentRepository
import com.example.docvault.data.repository.DocumentRepositoryImpl
import com.example.docvault.data.local.dao.DocumentDao
import com.example.docvault.data.local.dao.DocumentAccessDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDocumentRepository(
        documentDao: DocumentDao,
        documentAccessDao: DocumentAccessDao
    ): DocumentRepository {
        return DocumentRepositoryImpl(documentDao, documentAccessDao)
    }
}