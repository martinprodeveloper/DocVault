package com.example.docvault.domain.usecase

import com.example.docvault.domain.model.Document
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.fake.FakeDocumentRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DocumentUseCaseInsertTest {

    private lateinit var repository: FakeDocumentRepository
    private lateinit var useCase: DocumentUseCase

    @Before
    fun setup() {
        repository = FakeDocumentRepository()
        useCase = DocumentUseCase(repository)
    }

    @Test
    fun insertDocument_shouldStoreDocument() = runBlocking {

        val document = Document(
            id = "1",
            name = "Passport",
            type = DocumentType.PDF,
            path = "/files/passport.pdf"
        )

        useCase.insertDocument(document)

        val documents = useCase.getAllDocuments()

        assertEquals(1, documents.size)
        assertEquals("Passport", documents.first().name)
    }
}