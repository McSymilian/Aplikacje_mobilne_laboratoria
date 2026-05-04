package com.example.aplikacjatypulista_szczegy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplikacjatypulista_szczegy.routes.AppDatabase
import com.example.aplikacjatypulista_szczegy.routes.RouteRepository
import com.example.aplikacjatypulista_szczegy.ui.theme.AplikacjaTypuListaszczegolyTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouteViewModel(val repository: RouteRepository) : ViewModel() {
    val routes = repository.getRoutes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
    }

    class Factory(private val repository: RouteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(RouteViewModel::class.java)) { "Unknown ViewModel class" }
            return RouteViewModel(repository) as T
        }
    }
}

class MainActivity : ComponentActivity() {

    private val viewModel: RouteViewModel by viewModels {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = RouteRepository(db.routeDao(), db.routeTimerDao())
        RouteViewModel.Factory(repository)
    }

    @SuppressLint("ConfigurationScreenWidthHeight")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikacjaTypuListaszczegolyTheme {
                MainScreen(
                    viewModel = viewModel,
                    onNavigateToDetails = { routeId ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    viewModel: RouteViewModel,
    onNavigateToDetails: (Long) -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val containerWidthDp = with(density) { windowInfo.containerSize.width.toDp() }
    val isListDetail = containerWidthDp >= 600.dp

    var selectedRouteId by rememberSaveable { mutableStateOf<Long?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val routes by viewModel.routes.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Nawigacja", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                NavigationDrawerItem(
                    label = { Text("Trasy") },
                    selected = true,
                    onClick = { },
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
            }
        }
    ) {
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
                    RouteListScreenGrid(
                        routes = routes,
                        modifier = Modifier.weight(1f),
                        onRouteClick = { selectedRouteId = it }
                    )

                    VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    RouteDetailsPane(
                        repository = viewModel.repository,
                        routeId = selectedRouteId,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                RouteListScreenGrid(
                    routes = routes,
                    modifier = Modifier.padding(innerPadding),
                    onRouteClick = onNavigateToDetails
                )
            }
        }
    }
}

@Composable
private fun RouteListScreenGrid(
    routes: List<com.example.aplikacjatypulista_szczegy.routes.RouteEntity>,
    modifier: Modifier = Modifier,
    onRouteClick: (Long) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        val gridState = rememberLazyGridState()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            content = {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Aplikacja Górskich Szlaków", style = MaterialTheme.typography.headlineMedium)
                            Text(
                                "Ta aplikacja pomaga w śledzeniu postępów na szlakach turystycznych. Zobacz szczegóły wymarzonej trasy i mierz swój czas podejścia z użyciem zintegrowanego stopera.",
                                modifier = Modifier.padding(top = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(modifier = Modifier.weight(1f).clickable { /* TODO: Filtrowanie */ }) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Łatwe szlaki", style = MaterialTheme.typography.titleMedium)
                                Text("Dla początkujących", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                        Card(modifier = Modifier.weight(1f).clickable { /* TODO: Filtrowanie */ }) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Wymagające", style = MaterialTheme.typography.titleMedium)
                                Text("Dla zaawansowanych", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Dostępne trasy",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                    )
                }

                items(items = routes, key = { it.id }) { route ->
                    Card(modifier = Modifier
                        .padding(8.dp)
                        .clickable { onRouteClick(route.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Obrazek trasy",
                                    modifier = Modifier.fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(text = route.name, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        )
    }
}