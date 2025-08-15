package com.pranav.promptcraft.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ForceUpdateDialog(
    isCancellable: Boolean,
    updateUrl: String,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = {
            if (isCancellable) {
                onDismiss()
            } else {
                // If not cancellable and user tries to dismiss, close the app
                (context as? ComponentActivity)?.finish()
            }
        },
        title = {
            Text(
                text = "App Update Required",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "A newer version of the app is available. Please update to continue using the latest features and improvements.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // If Play Store app is not available, try opening in browser
                        try {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(browserIntent)
                        } catch (e: Exception) {
                            // Handle error - could show a toast or log
                        }
                    }
                }
            ) {
                Text("Update Now")
            }
        },
        dismissButton = if (isCancellable) {
            {
                TextButton(onClick = onDismiss) {
                    Text("Later")
                }
            }
        } else {
            null
        }
    )
}
