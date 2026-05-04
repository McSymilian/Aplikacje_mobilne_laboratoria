package com.example.aplikacjatypulista_szczegy.routes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RouteRepository(
    private val routeDao: RouteDao,
    private val routeTimerDao: RouteTimerDao
) {

    fun getRoutes(): Flow<List<RouteEntity>> = routeDao.getAll()

    fun getRouteById(routeId: Long): Flow<RouteEntity?> = routeDao.getById(routeId)

    fun getSavedElapsedSeconds(routeId: Long): Flow<Long> {
        return routeTimerDao.getByRouteId(routeId).map { it?.elapsedSeconds ?: 0L }
    }

    suspend fun saveElapsedSeconds(routeId: Long, elapsedSeconds: Long) {
        routeTimerDao.upsert(
            RouteTimerEntity(
                routeId = routeId,
                elapsedSeconds = elapsedSeconds
            )
        )
    }

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
                ),
                RouteEntity(
                    name = "Park Centralny 3 km",
                    type = "Biegowa",
                    description = "Krotka i dynamiczna petla po alejkach parku, dobra na rozgrzewke."
                ),
                RouteEntity(
                    name = "Bulwary Miejskie 7 km",
                    type = "Biegowa",
                    description = "Plaska trasa wzdluz bulwarow z rowna nawierzchnia i ladnym widokiem."
                ),
                RouteEntity(
                    name = "Lezny Interwal 8 km",
                    type = "Biegowa",
                    description = "Mieszana nawierzchnia i delikatne podbiegi, swietna pod trening tempowy."
                ),
                RouteEntity(
                    name = "Stare Miasto 6 km",
                    type = "Biegowa",
                    description = "Miejska trasa przez centrum, dobra na spokojne wybieganie."
                ),
                RouteEntity(
                    name = "Wzgorza Poldnia 12 km",
                    type = "Biegowa",
                    description = "Trasa z seria podbiegow i zbiegow, buduje sile i wytrzymalosc."
                ),
                RouteEntity(
                    name = "Petla Nad Zalewem 18 km",
                    type = "Rowerowa",
                    description = "Szybka petla wokol zalewu po asfalcie, idealna na rower szosowy."
                ),
                RouteEntity(
                    name = "Szutrowe Pola 22 km",
                    type = "Rowerowa",
                    description = "Lekki teren i szerokie drogi szutrowe, trasa dla gravela i MTB."
                ),
                RouteEntity(
                    name = "Miedzy Lasami 28 km",
                    type = "Rowerowa",
                    description = "Dlugie odcinki przez las z kilkoma technicznymi zakretami."
                ),
                RouteEntity(
                    name = "Podmiejskie Sprinty 16 km",
                    type = "Rowerowa",
                    description = "Krotkie odcinki szybkiej jazdy i nawroty, dobra na trening mocy."
                ),
                RouteEntity(
                    name = "Kotlina i Grzbiet 35 km",
                    type = "Rowerowa",
                    description = "Zroznicowana trasa z podjazdem na grzbiet i dlugim zjazdem."
                ),
                RouteEntity(
                    name = "Rzeczne Meandry 14 km",
                    type = "Rowerowa",
                    description = "Malownicza trasa z odcinkami przy wodzie i kilkoma mostkami."
                ),
                RouteEntity(
                    name = "Nocna Obwodnica 9 km",
                    type = "Biegowa",
                    description = "Oswietlona trasa miejska, wygodna do wieczornego treningu."
                ),
                RouteEntity(
                    name = "Skraj Lasu 11 km",
                    type = "Biegowa",
                    description = "Urozmaicone podloze na obrzezach lasu, spokojna i cicha okolica."
                ),
                RouteEntity(
                    name = "Pagorki Zachodu 20 km",
                    type = "Rowerowa",
                    description = "Falujaca trasa z seria krotszych podjazdow i szybkich prostych."
                ),
                RouteEntity(
                    name = "Cztery Mosty 13 km",
                    type = "Biegowa",
                    description = "Petla laczaca cztery przeprawy rzeczne, rowny i przyjemny rytm biegu."
                )
            )
        )
    }
}
