package com.taskpulse.app

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.taskpulse.app.navigation.TaskPulseNavGraph
import com.taskpulse.app.ui.theme.TaskPulseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    private fun isUsageAccessGranted(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
            } else {
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (exception: Exception) {
            false
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskPulseTheme {
                val context = LocalContext.current
                val showUsagePrompt = remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    if (!isUsageAccessGranted(context)) {
                        showUsagePrompt.value = true
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notificationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    if (notificationPermission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                if (showUsagePrompt.value) {
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }

                TaskPulseNavGraph()
            }
        }
    }
}
