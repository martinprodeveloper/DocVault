package com.example.docvault.presentation.screens

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.docvault.domain.model.DocumentType
import com.example.docvault.presentation.states.DocumentDetailUiState
import com.example.docvault.presentation.utils.AppUtils
import com.example.docvault.presentation.viewmodels.DocumentDetailViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@Composable
fun DocumentDetailScreen(
    documentId: String,
    onBack: () -> Unit,
    viewModel: DocumentDetailViewModel = hiltViewModel(),
    locationAddress: String? = null
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    DisposableEffect(Unit) {
        (context as? Activity)?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        onDispose {
            (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    val state by viewModel.uiState.collectAsState()
    var scale by remember { mutableFloatStateOf(1f) }

    val executor = remember { Executors.newSingleThreadExecutor() }
    val canAuthenticate = BiometricManager.from(context).canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
    ) == BiometricManager.BIOMETRIC_SUCCESS

    val biometricPrompt = remember(activity) {
        activity?.let { act ->
            BiometricPrompt(act, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(context, "Authentication succeeded", Toast.LENGTH_SHORT).show()
                        viewModel.onBiometricSuccess(documentId, locationAddress)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setSubtitle("Access this document")
            .setNegativeButtonText("Cancel")
            .build()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "DOCUMENT DETAIL",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is DocumentDetailUiState.Loading -> CircularProgressIndicator()

                is DocumentDetailUiState.RequireBiometric -> {
                    if (canAuthenticate) {
                        Button(onClick = { biometricPrompt?.authenticate(promptInfo) }) {
                            Text("Authenticate with Biometrics")
                        }
                    } else {
                        Text(
                            text = "Biometric authentication not supported",
                            modifier = Modifier.padding(horizontal = 20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is DocumentDetailUiState.Success -> {
                    val document = currentState.document
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = document.name ?: "DOCUMENT",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (document.type == DocumentType.IMAGE && document.path != null) {
                            val bitmap = BitmapFactory.decodeFile(File(document.path).absolutePath)
                            bitmap?.let { originalBitmap ->
                                val scaledBitmap = AppUtils.scaleBitmap(originalBitmap, 1200, 1200)
                                val watermarkedBitmap = AppUtils.addWatermark(scaledBitmap, locationAddress)
                                Image(
                                    bitmap = watermarkedBitmap.asImageBitmap(),
                                    contentDescription = document.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp)
                                        .pointerInput(Unit) {
                                            detectTransformGestures { _, _, zoom, _ ->
                                                scale = (scale * zoom).coerceIn(1f, 5f)
                                            }
                                        }
                                        .scale(scale)
                                )
                            }
                        } else {
                            Text(
                                text = "PDF preview not implemented or path is null",
                                modifier = Modifier.padding(horizontal = 20.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (currentState.accessLogs.isNotEmpty()) {
                            Column {
                                Text(
                                    "Last Accesses:",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                currentState.accessLogs.forEach { log ->
                                    val date = log.timestamp?.let {
                                        SimpleDateFormat(
                                            "yyyy-MM-dd HH:mm:ss",
                                            Locale.getDefault()
                                        ).format(Date(it))
                                    } ?: "-"
                                    val location = log.locationAddress ?: "-"
                                    Text("$date - ${log.action ?: "-"} - $location")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.deleteDocument(documentId, locationAddress) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                "Delete Document",
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }

                is DocumentDetailUiState.Deleted -> Text(
                    "DOCUMENT DELETED",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center
                )

                is DocumentDetailUiState.Error -> {
                    Text(
                        text = "ERROR: ${currentState.message}",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        textAlign = TextAlign.Center
                    )
                    Toast.makeText(context, "Error: ${currentState.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    BackHandler { onBack() }
}