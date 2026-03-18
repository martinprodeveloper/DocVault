package com.example.docvault.domain.usecase

import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.fake.FakeDocumentRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DocumentUseCaseDeleteTest {

    private lateinit var repository: FakeDocumentRepository
    private lateinit var useCase: DocumentUseCase

    @Before
    fun setup() {
        repository = FakeDocumentRepository()
        useCase = DocumentUseCase(repository)
    }

    @Test
    fun deleteDocument_shouldRemoveDocument() = runBlocking {

        val document = Document(
            id = "1",
            name = "Secret File",
            type = DocumentType.PDF,
            path = "/files/secret.pdf"
        )

        repository.insertDocument(document)

        useCase.deleteDocument("1")

        val documents = useCase.getAllDocuments()

        assertEquals(0, documents.size)
    }
}