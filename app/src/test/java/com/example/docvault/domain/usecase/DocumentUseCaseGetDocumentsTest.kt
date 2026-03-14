package com.example.docvault.domain.usecase

import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.fake.FakeDocumentRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DocumentUseCaseGetDocumentsTest {

    private lateinit var repository: FakeDocumentRepository
    private lateinit var useCase: DocumentUseCase

    @Before
    fun setup() {
        repository = FakeDocumentRepository()
        useCase = DocumentUseCase(repository)
    }

    @Test
    fun getAllDocuments_shouldReturnStoredDocuments() = runBlocking {

        repository.insertDocument(
            Document(
                id = "1",
                name = "ID Card",
                type = DocumentType.IMAGE,
                path = "/files/id.jpg"
            )
        )

        repository.insertDocument(
            Document(
                id = "2",
                name = "Contract",
                type = DocumentType.PDF,
                path = "/files/contract.pdf"
            )
        )

        val documents = useCase.getAllDocuments()

        assertEquals(2, documents.size)
    }
}