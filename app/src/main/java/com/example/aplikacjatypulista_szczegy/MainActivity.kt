package com.example.aplikacjatypulista_szczegy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.lifecycle.lifecycleScope
import com.example.aplikacjatypulista_szczegy.routes.AppDatabase
import com.example.aplikacjatypulista_szczegy.routes.RouteEntity
import com.example.aplikacjatypulista_szczegy.routes.RouteRepository
import com.example.aplikacjatypulista_szczegy.ui.theme.AplikacjaTypuListaszczegolyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("ConfigurationScreenWidthHeight")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getInstance(this)
        val repository = RouteRepository(db.routeDao(), db.routeTimerDao())

        lifecycleScope.launch {
            repository.seedIfEmpty()
        }

        enableEdgeToEdge()
        setContent {
            AplikacjaTypuListaszczegolyTheme {
                val windowInfo = LocalWindowInfo.current
                val density = LocalDensity.current
                val containerWidthDp = with(density) { windowInfo.containerSize.width.toDp() }
                val containerHeightDp = with(density) { windowInfo.containerSize.height.toDp() }
                val isListDetail = containerWidthDp >= containerHeightDp && containerWidthDp >= 600.dp
                var selectedRouteId by rememberSaveable { mutableStateOf<Long?>(null) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { CenterAlignedTopAppBar(title = { Text("Trasy") }) }
                ) { innerPadding ->
                    if (isListDetail) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            RouteListScreen(
                                repository = repository,
                                modifier = Modifier.weight(1f),
                                onRouteClick = { selectedRouteId = it }
                            )

                            VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            RoutePreviewPane(
                                repository = repository,
                                routeId = selectedRouteId,
                                onOpenFullDetails = { routeId ->
                                    val detailsIntent = Intent(
                                        this@MainActivity,
                                        RouteDetailsActivity::class.java
                                    ).putExtra(RouteDetailsActivity.EXTRA_ROUTE_ID, routeId)
                                    startActivity(detailsIntent)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        RouteListScreen(
                            repository = repository,
                            modifier = Modifier.padding(innerPadding),
                            onRouteClick = { routeId ->
                                val detailsIntent = Intent(
                                    this@MainActivity,
                                    RouteDetailsActivity::class.java
                                ).putExtra(RouteDetailsActivity.EXTRA_ROUTE_ID, routeId)
                                startActivity(detailsIntent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteListScreen(
    repository: RouteRepository,
    modifier: Modifier = Modifier,
    onRouteClick: (Long) -> Unit
) {
    val routes by repository.getRoutes().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(routes, key = { it.id }) { route ->
            RouteListItem(route = route, onClick = { onRouteClick(route.id) })
        }
    }
}

@Composable
private fun RoutePreviewPane(
    repository: RouteRepository,
    routeId: Long?,
    onOpenFullDetails: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val route by repository
        .getRouteById(routeId ?: -1L)
        .collectAsState(initial = null)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (route == null) {
            Text(text = "Wybierz trase z listy")
            return@Box
        }
        val selectedRoute = route ?: return@Box

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = selectedRoute.name)
                Text(text = "Typ: ${selectedRoute.type}", modifier = Modifier.padding(top = 8.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text(text = selectedRoute.description)
                FilledTonalButton(
                    onClick = { onOpenFullDetails(selectedRoute.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Otworz pelne szczegoly i stoper")
                }
            }
        }
    }
}

@Composable
private fun RouteListItem(route: RouteEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = route.name)
            Text(text = "Typ: ${route.type}")
        }
    }
}