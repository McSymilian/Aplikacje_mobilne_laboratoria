package com.example.aplikacjatypulista_szczegy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import com.example.aplikacjatypulista_szczegy.routes.AppDatabase
import com.example.aplikacjatypulista_szczegy.routes.RouteEntity
import com.example.aplikacjatypulista_szczegy.routes.RouteRepository
import com.example.aplikacjatypulista_szczegy.ui.theme.AplikacjaTypuListaszczegolyTheme

class RouteDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val routeId = intent.getLongExtra(EXTRA_ROUTE_ID, -1L)
        val db = AppDatabase.getInstance(this)
        val repository = RouteRepository(db.routeDao(), db.routeTimerDao())
        val stopwatchViewModel = ViewModelProvider(
            this,
            RouteDetailsViewModel.Factory(repository, routeId)
        )[RouteDetailsViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            AplikacjaTypuListaszczegolyTheme {
                RouteDetailsScreen(
                    repository = repository,
                    stopwatchViewModel = stopwatchViewModel,
                    routeId = routeId,
                    showTopBar = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    companion object {
        const val EXTRA_ROUTE_ID = "route_id"
    }
}

@Composable
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
fun RouteDetailsScreen(
    repository: RouteRepository,
    stopwatchViewModel: RouteDetailsViewModel,
    routeId: Long,
    showTopBar: Boolean = true,
    modifier: Modifier = Modifier
) {
    val route by repository.getRouteById(routeId).collectAsState(initial = null)
    val stopwatchState by stopwatchViewModel.stopwatchState.collectAsState()
    val context = LocalContext.current

    if (route == null) {
        Text(
            text = "Nie znaleziono trasy.",
            modifier = modifier.padding(16.dp)
        )
        return
    }
    val currentRoute = route ?: return

    Scaffold(
        modifier = modifier,
        topBar = if (showTopBar) {
            { TopAppBar(title = { Text(text = currentRoute.name) }) }
        } else {
            {}
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val text = "Trasa: ${currentRoute.name}\nCzas: ${formatElapsedTime(stopwatchState.elapsedSeconds)}"
                val sendIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                val chooser = android.content.Intent.createChooser(sendIntent, "Wyślij czas")
                context.startActivity(chooser)
            }) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = "Wyślij czas")
            }
        }
    ) { innerPadding ->
        RouteDetailsContent(
            route = currentRoute,
            stopwatchState = stopwatchState,
            onStart = stopwatchViewModel::onStart,
            onStop = stopwatchViewModel::onStop,
            onInterrupt = stopwatchViewModel::onInterrupt,
            modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun RouteDetailsPane(
    modifier: Modifier = Modifier,
    repository: RouteRepository,
    routeId: Long?
) {
    val id = routeId ?: -1L
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val vm = remember(activity, id) {
        activity?.let {
            ViewModelProvider(it, RouteDetailsViewModel.Factory(repository, id)).get(id.toString(), RouteDetailsViewModel::class.java)
        }
    }

    if (vm != null) {
        RouteDetailsScreen(
            repository = repository,
            stopwatchViewModel = vm,
            routeId = id,
            showTopBar = false,
            modifier = modifier
        )
    } else {
        Text(text = "Brak kontekstu do utworzenia ViewModel")
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun RouteDetailsContent(
    route: RouteEntity,
    stopwatchState: StopwatchUiState,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onInterrupt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val widthClass = widthClassFrom(configuration.screenWidthDp)
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    val containerPadding = when (widthClass) {
        WidthClass.Compact -> 16.dp
        WidthClass.Medium -> 20.dp
        WidthClass.Expanded -> 24.dp
    }
    val maxContentWidth = when (widthClass) {
        WidthClass.Compact -> 640.dp
        WidthClass.Medium -> 860.dp
        WidthClass.Expanded -> 1100.dp
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(containerPadding),
        contentAlignment = Alignment.TopCenter
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = maxContentWidth)
        ) {
            RouteInfoCard(
                route = route,
                widthClass = widthClass
            )
            Spacer(modifier = Modifier.height(16.dp))
            StopwatchCard(
                state = stopwatchState,
                onStart = onStart,
                onStop = onStop,
                onInterrupt = onInterrupt,
                widthClass = widthClass,
                isLandscape = isLandscape
            )
        }

    }
}

@Composable
private fun RouteInfoCard(
    route: RouteEntity,
    widthClass: WidthClass,
    modifier: Modifier = Modifier
) {
    val cardPadding = when (widthClass) {
        WidthClass.Compact -> 16.dp
        WidthClass.Medium -> 20.dp
        WidthClass.Expanded -> 24.dp
    }

    val nameStyle = when (widthClass) {
        WidthClass.Expanded -> MaterialTheme.typography.headlineSmall
        else -> MaterialTheme.typography.titleLarge
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(cardPadding)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Zdjecie szczegolowe trasy",
                    modifier = Modifier.fillMaxSize(),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(text = route.name, style = nameStyle)
            Text(text = "Typ: ${route.type}", modifier = Modifier.padding(top = 8.dp))
            Text(text = route.description, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
private fun StopwatchCard(
    state: StopwatchUiState,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onInterrupt: () -> Unit,
    widthClass: WidthClass,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val cardPadding = when (widthClass) {
        WidthClass.Compact -> 16.dp
        WidthClass.Medium -> 20.dp
        WidthClass.Expanded -> 24.dp
    }

    val timeStyle = when (widthClass) {
        WidthClass.Expanded -> MaterialTheme.typography.headlineLarge
        else -> MaterialTheme.typography.headlineMedium
    }

    val compactLandscape = widthClass == WidthClass.Compact && isLandscape

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(cardPadding)) {
            Text(text = "Stoper", style = MaterialTheme.typography.titleMedium)
            Text(
                text = formatElapsedTime(state.elapsedSeconds),
                style = timeStyle,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (compactLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onStart,
                        enabled = !state.isRunning,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Start", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    OutlinedButton(
                        onClick = onStop,
                        enabled = state.isRunning,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Filled.Pause, contentDescription = "Stop")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Stop", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                OutlinedButton(
                    onClick = onInterrupt,
                    enabled = state.isRunning || state.elapsedSeconds > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Przerwij")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Przerwij", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onStart,
                        enabled = !state.isRunning,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Start", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    OutlinedButton(
                        onClick = onStop,
                        enabled = state.isRunning,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Filled.Pause, contentDescription = "Stop")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Stop", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    OutlinedButton(
                        onClick = onInterrupt,
                        enabled = state.isRunning || state.elapsedSeconds > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Przerwij")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Przerwij", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            val statusText = if (state.isRunning) "Status: uruchomiony" else "Status: zatrzymany"
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.End)
            )
        }
    }
}

private enum class WidthClass {
    Compact,
    Medium,
    Expanded
}

private fun widthClassFrom(screenWidthDp: Int): WidthClass {
    return when {
        screenWidthDp >= 840 -> WidthClass.Expanded
        screenWidthDp >= 600 -> WidthClass.Medium
        else -> WidthClass.Compact
    }
}

private fun formatElapsedTime(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}


