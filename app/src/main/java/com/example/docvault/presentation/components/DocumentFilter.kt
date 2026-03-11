package com.example.docvault.presentation.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.docvault.domain.model.DocumentType

@Composable
fun DocumentFilter(
    selectedFilter: DocumentType?,
    onFilterSelected: (DocumentType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == null,
            onClick = { onFilterSelected(null) },
            label = { Text("ALL") }
        )

        FilterChip(
            selected = selectedFilter == DocumentType.PDF,
            onClick = { onFilterSelected(DocumentType.PDF) },
            label = { Text("PDF") }
        )

        FilterChip(
            selected = selectedFilter == DocumentType.IMAGE,
            onClick = { onFilterSelected(DocumentType.IMAGE) },
            label = { Text("IMAGE") }
        )
    }
}