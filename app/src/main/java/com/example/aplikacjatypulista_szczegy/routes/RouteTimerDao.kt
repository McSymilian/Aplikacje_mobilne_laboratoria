package com.example.aplikacjatypulista_szczegy.routes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteTimerDao {
    @Query("SELECT * FROM route_timers WHERE routeId = :routeId")
    fun getByRouteId(routeId: Long): Flow<RouteTimerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(timer: RouteTimerEntity)
}
