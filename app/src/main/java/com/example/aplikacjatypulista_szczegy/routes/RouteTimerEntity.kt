package com.example.aplikacjatypulista_szczegy.routes

import androidx.room.Entity

@Entity(tableName = "route_timers", primaryKeys = ["routeId"])
data class RouteTimerEntity(
    val routeId: Long,
    val elapsedSeconds: Long
)
