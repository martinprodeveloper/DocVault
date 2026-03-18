package com.example.docvault.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.docvault.presentation.screens.DocumentsScreen
import com.example.docvault.presentation.screens.DocumentDetailScreen

@Composable
fun DocVaultNavHost(
    navController: NavHostController,
    context: Context,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Routes.DOCUMENTS,
        modifier = modifier
    ) {

        composable(Routes.DOCUMENTS) {
            DocumentsScreen(
                context = context,
                onDocumentClick = { documentId ->
                    navController.navigate(Routes.documentDetail(documentId))
                }
            )
        }

        composable(
            route = Routes.DOCUMENT_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(Routes.DOCUMENT_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val documentId =
                backStackEntry.arguments?.getString(Routes.DOCUMENT_ID_ARG) ?: ""

            DocumentDetailScreen(
                documentId = documentId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}