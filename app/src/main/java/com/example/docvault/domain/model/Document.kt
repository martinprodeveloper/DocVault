package com.example.docvault.domain.model

data class Document(
    val id: String? = null,
    val name: String? = null,
    val type: DocumentType? = null,
    val path: String? = null,
    val lastAccess: Long? = null
)

enum class DocumentType {
    PDF,
    IMAGE
}