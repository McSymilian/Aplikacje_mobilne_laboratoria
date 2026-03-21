package com.example.aplikacjatypulista_szczegy.routes

import kotlinx.coroutines.flow.Flow

class RouteRepository(private val routeDao: RouteDao) {

    fun getRoutes(): Flow<List<RouteEntity>> = routeDao.getAll()

    fun getRouteById(routeId: Long): Flow<RouteEntity?> = routeDao.getById(routeId)

    suspend fun seedIfEmpty() {
        if (routeDao.count() > 20) return

        routeDao.insertAll(
            listOf(
                RouteEntity(
                    name = "Las Miejski 5 km",
                    type = "Biegowa",
                    description = "Latwa trasa biegowa po utwardzonych sciezkach, idealna na szybki trening."
                ),
                RouteEntity(
                    name = "Wokol Jeziora 10 km",
                    type = "Biegowa",
                    description = "Przyjemna petla z kilkoma podbiegami i punktami widokowymi."
                ),
                RouteEntity(
                    name = "Szlak Rzeczny 25 km",
                    type = "Rowerowa",
                    description = "Malownicza trasa rowerowa wzdluz rzeki, glownie asfalt i szuter."
                ),
                RouteEntity(
                    name = "Gorskie Podjazdy 40 km",
                    type = "Rowerowa",
                    description = "Wymagajaca trasa z dlugimi podjazdami i szybkim zjazdem na koncu."
                )
            )
        )
    }
}

