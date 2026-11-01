package com.taskpulse.app.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.taskpulse.app.R
import com.taskpulse.app.util.ReportExportHelper
import com.taskpulse.app.viewmodel.AuthUiState
import com.taskpulse.app.viewmodel.AuthViewModel
import com.taskpulse.app.viewmodel.DashboardUiState
import com.taskpulse.app.viewmodel.DashboardViewModel
import com.taskpulse.app.viewmodel.SettingsUiState
import com.taskpulse.app.viewmodel.SettingsViewModel
import com.taskpulse.app.viewmodel.UsageUiState
import com.taskpulse.app.viewmodel.UsageViewModel
import java.util.Locale
import kotlin.math.min

@Composable
fun SplashScreen(onContinue: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pulse))
    val progress by animateFloatAsState(targetValue = 1f, animationSpec = tween(durationMillis = 2200, easing = LinearEasing))

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2200)
        onContinue()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LottieAnimation(composition, progress, modifier = Modifier.size(220.dp))
                Spacer(modifier = Modifier.height(22.dp))
                Text("TaskPulse", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Next-gen phone health, usage analytics and productivity coach.", textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 28.dp))
            }
        }
    }
}

@Composable
fun OnboardingScreen(onContinue: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    val titles = listOf("Smart Performance", "Usage Intelligence", "Focus & Goals")
    val subtitles = listOf(
        "Monitor battery, RAM, storage and device health in one dashboard.",
        "Track app time, usage categories and productivity scores.",
        "Unlock focus sessions, study reminders and daily progress insights."
    )
    val gradients = listOf(
        Brush.linearGradient(listOf(Color(0xFF0D47A1), Color(0xFF1976D2))),
        Brush.linearGradient(listOf(Color(0xFF00838F), Color(0xFF26C6DA))),
        Brush.linearGradient(listOf(Color(0xFF6A1B9A), Color(0xFFAB47BC)))
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(gradients[page])
            .padding(28.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Spacer(modifier = Modifier.height(4.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(titles[page], color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(subtitles[page], color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.bodyLarge)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.White.copy(alpha = 0.14f), RoundedCornerShape(28.dp))) {
                    Text("Premium dashboard preview", modifier = Modifier.align(Alignment.Center), color = Color.White.copy(alpha = 0.8f))
                }
                Spacer(modifier = Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { page = (page + 2) % 3 }, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.16f))) {
                        Text("Previous", color = Color.White)
                    }
                    Button(onClick = { if (page == 2) onContinue() else page = page + 1 }) {
                        Text(if (page == 2) "Get Started" else "Next")
                    }
                }
            }
        }
    }
}

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState().value
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome Back", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Sign in to unlock your dashboard and personalized productivity insights.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(22.dp))
            Button(onClick = { viewModel.login(email, password) }, modifier = Modifier.fillMaxWidth(), enabled = !state.isLoading) {
                Text(if (state.isLoading) "Signing in..." else "Sign In")
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { viewModel.signUp(email, password) }) { Text("Create a new account") }
            TextButton(onClick = { viewModel.forgotPassword(email) }) { Text("Forgot password?") }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { viewModel.signInWithGoogle() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Text("Continue with Google")
            }
            Spacer(modifier = Modifier.height(14.dp))
            state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            state.statusMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            if (state.isAuthenticated) {
                LaunchedEffect(state.isAuthenticated) { onLoginSuccess() }
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: DashboardViewModel, onNavigate: (String) -> Unit) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold(topBar = { AppTopBar(title = "Dashboard") }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)) {
            SectionLabel("Today’s Performance")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                StatusCard("Battery", "${state.batteryPercentage}%", Color(0xFF4CAF50))
                StatusCard("RAM", "${state.memoryUsagePercent}%", Color(0xFF42A5F5))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                StatusCard("Storage", "${state.storageUsedPercent}%", Color(0xFFFFA726))
                StatusCard("CPU", "${state.cpuUsagePercent}%", Color(0xFFAB47BC))
            }
            Spacer(modifier = Modifier.height(18.dp))
            PerformanceRing(score = state.performanceScore)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Smart suggestions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            SuggestionChip("Reduce social media usage", Color(0xFF81C784))
            SuggestionChip("Try focus mode for 25 minutes", Color(0xFF64B5F6))
            SuggestionChip("Clear cache for storage boost", Color(0xFFFFB74D))
            Spacer(modifier = Modifier.height(28.dp))
            SectionLabel("Recent app usage")
            state.usageStats.take(3).forEach { item ->
                MiniUsageTile(item.appName, item.category, item.minutesUsed, item.productivityScore)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile("Usage", Color(0xFF1976D2)) { onNavigate("usage") }
                ActionTile("Focus", Color(0xFF6A1B9A)) { onNavigate("focus") }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile("Battery", Color(0xFF388E3C)) { onNavigate("battery") }
                ActionTile("Reports", Color(0xFFEF6C00)) { onNavigate("reports") }
            }
        }
    }
}

@Composable
fun UsageScreen(viewModel: UsageViewModel, onNavigate: (String) -> Unit) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold(topBar = { AppTopBar(title = "App Usage") }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)) {
            Text("Track time spent across apps and categories.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
            Spacer(modifier = Modifier.height(18.dp))
            val usageStats = state.usageStats
            if (usageStats.isEmpty()) {
                Text("Usage stats loading...", color = MaterialTheme.colorScheme.onBackground)
            } else {
                usageStats.forEach { item ->
                    MiniUsageTile(item.appName, item.category, item.minutesUsed, item.productivityScore)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(onClick = { viewModel.refreshUsageStats() }, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.isRefreshing) "Refreshing…" else "Refresh usage data")
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text("Today’s highlights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            UsageInsightTile("Top category", "Social media", "Most time spent")
            UsageInsightTile("Addiction score", "Moderate", "Based on today’s app opens")
            UsageInsightTile("Productivity score", "72", "Balanced focus sessions")
        }
    }
}

@Composable
fun AnalyticsScreen(onNavigate: (String) -> Unit) {
    Scaffold(topBar = { AppTopBar(title = "Analytics") }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)) {
            Text("Your weekly trends at a glance.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
            Spacer(modifier = Modifier.height(20.dp))
            ChartCard("Screen time", 78, Color(0xFF42A5F5))
            ChartCard("Battery trend", 62, Color(0xFF66BB6A))
            ChartCard("Productivity", 71, Color(0xFFAB47BC))
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { onNavigate("reports") }, modifier = Modifier.fillMaxWidth()) {
                Text("View full report")
            }
        }
    }
}

@Composable
fun BatteryScreen(onNavigate: (String) -> Unit) {
    Scaffold(topBar = { AppTopBar(title = "Battery") }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)) {
            Text("Keep your battery healthy with smart tips.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
            Spacer(modifier = Modifier.height(18.dp))
            StatusCard("Battery health", "Good", Color(0xFF66BB6A))
            Spacer(modifier = Modifier.height(16.dp))
            InfoTile("Charge screen", "Keep brightness under 70% and limit background refresh.")
            InfoTile("Optimize mode", "Enable power-saving suggestions for longer sessions.")
        }
    }
}

@Composable
fun RamScreen(onNavigate: (String) -> Unit) {
    Scaffold(topBar = { AppTopBar(title = "Memory") }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)) {
            Text("RAM usage in real time with gentle optimization advice.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
            Spacer(modifier = Modifier.height(18.dp))
            InfoTile("Background pressure", "Light apps running in background can be managed safely.")
            InfoTile("Smart cleanup", "Use TaskPulse suggestions before forcing system cleanup.")
        }
    }
}

@Composable
fun FocusScreen(onNavigate: (String) -> Unit) {
    var isRunning by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1200))

    Scaffold(topBar = { AppTopBar(title = "Focus Mode") }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Start a focus session and minimize distractions.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = Color(0xFF1E88E5).copy(alpha = 0.2f), radius = size.minDimension / 2)
                    drawCircle(color = Color(0xFF1E88E5), radius = (size.minDimension / 2) * animatedProgress, style = Stroke(width = 18f))
                }
                Text(if (isRunning) "Focus Active" else "Ready", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                isRunning = !isRunning
                progress = if (isRunning) 0.82f else 0f
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (isRunning) "Stop session" else "Start 25 min Focus")
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text("Daily streak: 5 days", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ReportsScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    Scaffold(topBar = { AppTopBar(title = "Reports") }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Export usage and health trends to PDF or share with your team.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
            ReportTile("Daily report", "Screen time, battery, and productivity insights.")
            ReportTile("Weekly summary", "Growth charts and focus session analytics.")
            ReportTile("Monthly trend", "Track habits and app usage behavior.")
            Button(onClick = {
                val reportText = "TaskPulse Report\n\nBattery status, RAM health, usage trends, and productivity insights."
                ReportExportHelper.createPdfReport(context, "TaskPulse Report", reportText)?.let { uri ->
                    ReportExportHelper.sharePdfReport(context, uri)
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Export PDF")
            }
        }
    }
}

@Composable
fun ProfileScreen(onNavigate: (String) -> Unit, authViewModel: AuthViewModel = hiltViewModel()) {
    val authState = authViewModel.uiState.collectAsState().value
    Scaffold(topBar = { AppTopBar(title = "Profile") }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("TaskPulse Premium", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Get personalized insights, habit coaching and performance alerts.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
            InfoTile("Signed in as", authState.userEmail.ifBlank { "guest@taskpulse.ai" })
            InfoTile("Subscription", "Free plan with premium upgrade available")
            Button(onClick = { onNavigate("settings") }, modifier = Modifier.fillMaxWidth()) {
                Text("Settings")
            }
            Button(onClick = { authViewModel.logout(); onNavigate("auth") }, modifier = Modifier.fillMaxWidth()) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold(topBar = { AppTopBar(title = "Settings") }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Customize your TaskPulse experience.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
            ToggleRow("Dark mode", state.darkModeEnabled) { viewModel.toggleDarkMode() }
            ToggleRow("Notifications", state.notificationsEnabled) { viewModel.toggleNotifications() }
            ToggleRow("Language", state.language == "English") { viewModel.toggleLanguage() }
            Button(onClick = { viewModel.exportData() }, modifier = Modifier.fillMaxWidth()) {
                Text("Export data")
            }
            Button(onClick = { viewModel.backupData() }, modifier = Modifier.fillMaxWidth()) {
                Text("Backup data")
            }
            Button(onClick = { viewModel.restoreData() }, modifier = Modifier.fillMaxWidth()) {
                Text("Restore data")
            }
            Button(onClick = { viewModel.clearHistory() }, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.historyCleared) "Cleared" else "Clear history")
            }
            state.exportStatusMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            state.backupStatusMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            state.restoreStatusMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
fun AppTopBar(title: String) {
    TopAppBar(title = { Text(title, fontWeight = FontWeight.Bold) }, colors = androidx.compose.material3.TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface))
}

@Composable
fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
}

@Composable
fun StatusCard(label: String, value: String, accent: Color) {
    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.weight(1f), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = accent)
        }
    }
}

@Composable
fun PerformanceRing(score: Int) {
    Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Performance score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(18.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), startAngle = -90f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 18f, cap = StrokeCap.Round))
                    drawArc(color = MaterialTheme.colorScheme.primary, startAngle = -90f, sweepAngle = score * 3.6f, useCenter = false, style = Stroke(width = 18f, cap = StrokeCap.Round))
                }
                Text("$score", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("AI productivity pulse", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SuggestionChip(text: String, color: Color) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .background(color.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
        .padding(16.dp)) {
        Text(text, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MiniUsageTile(name: String, category: String, minutes: Int, score: Int) {
    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(46.dp)
                .background(Color(0xFF1976D2), CircleShape), contentAlignment = Alignment.Center) {
                Text(name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold)
                Text(category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${minutes}m", fontWeight = FontWeight.Bold)
                Text("Score $score", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ActionTile(label: String, accent: Color, onTap: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.weight(1f).clickable { onTap() }, elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontWeight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
fun InfoTile(title: String, subtitle: String) {
    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun UsageInsightTile(title: String, value: String, subtitle: String) {
    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ChartCard(title: String, percent: Int, tint: Color) {
    Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .size(72.dp)
                    .background(tint.copy(alpha = 0.18f), CircleShape), contentAlignment = Alignment.Center) {
                    Text("$percent%", fontWeight = FontWeight.ExtraBold, color = tint)
                }
                Spacer(modifier = Modifier.width(18.dp))
                LinearProgressIndicator(progress = percent / 100f, modifier = Modifier.fillMaxWidth(), color = tint)
            }
        }
    }
}

@Composable
fun ReportTile(title: String, subtitle: String) {
    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ToggleRow(label: String, enabled: Boolean, onToggle: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onToggle() }
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp))
        .padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(label, fontWeight = FontWeight.SemiBold)
            Text(if (enabled) "Enabled" else "Disabled", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(modifier = Modifier
            .size(38.dp)
            .background(if (enabled) Color(0xFF4CAF50) else Color(0xFFB0BEC5), CircleShape))
    }
}
