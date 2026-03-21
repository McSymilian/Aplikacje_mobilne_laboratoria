package com.example.aplikacjatypulista_szczegy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.aplikacjatypulista_szczegy.routes.AppDatabase
import com.example.aplikacjatypulista_szczegy.routes.RouteEntity
import com.example.aplikacjatypulista_szczegy.routes.RouteRepository
import com.example.aplikacjatypulista_szczegy.ui.theme.AplikacjaTypuListaszczegolyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = RouteRepository(AppDatabase.getInstance(this).routeDao())

        lifecycleScope.launch {
            repository.seedIfEmpty()
        }

        enableEdgeToEdge()
        setContent {
            AplikacjaTypuListaszczegolyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RouteListScreen(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding),
                        onRouteClick = { routeId ->
                            val detailsIntent = Intent(this, RouteDetailsActivity::class.java)
                                .putExtra(RouteDetailsActivity.EXTRA_ROUTE_ID, routeId)
                            startActivity(detailsIntent)
                        }
                    )
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