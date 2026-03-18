package com.example.docvault.domain.model

data class DocumentAccess(
    val documentId: String? = null,
    val timestamp: Long? = null,
    val action: AccessAction? = null,
    val locationAddress: String? = null
)

enum class AccessAction {
    VIEWED,
    DELETED
}