package com.example.aplikacjatypulista_szczegy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aplikacjatypulista_szczegy.routes.AppDatabase
import com.example.aplikacjatypulista_szczegy.routes.RouteEntity
import com.example.aplikacjatypulista_szczegy.routes.RouteRepository
import com.example.aplikacjatypulista_szczegy.ui.theme.AplikacjaTypuListaszczegolyTheme

class RouteDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val routeId = intent.getLongExtra(EXTRA_ROUTE_ID, -1L)
        val repository = RouteRepository(AppDatabase.getInstance(this).routeDao())

        enableEdgeToEdge()
        setContent {
            AplikacjaTypuListaszczegolyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RouteDetailsScreen(
                        repository = repository,
                        routeId = routeId,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_ROUTE_ID = "route_id"
    }
}

@Composable
fun RouteDetailsScreen(
    repository: RouteRepository,
    routeId: Long,
    modifier: Modifier = Modifier
) {
    val route by repository.getRouteById(routeId).collectAsState(initial = null)

    if (route == null) {
        Text(
            text = "Nie znaleziono trasy.",
            modifier = modifier.padding(16.dp)
        )
        return
    }

    RouteDetailsContent(route = route!!, modifier = modifier)
}

@Composable
private fun RouteDetailsContent(route: RouteEntity, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = route.name)
        Text(text = "Typ: ${route.type}", modifier = Modifier.padding(top = 8.dp))
        Text(text = route.description, modifier = Modifier.padding(top = 12.dp))
    }
}

