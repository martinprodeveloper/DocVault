package com.example.docvault.presentation.navigation

object Routes {

    const val DOCUMENTS = "documents"

    const val DOCUMENT_DETAIL = "document_detail"

    const val DOCUMENT_ID_ARG = "documentId"

    const val DOCUMENT_DETAIL_ROUTE =
        "$DOCUMENT_DETAIL/{$DOCUMENT_ID_ARG}"

    fun documentDetail(documentId: String): String {
        return "$DOCUMENT_DETAIL/$documentId"
    }
}