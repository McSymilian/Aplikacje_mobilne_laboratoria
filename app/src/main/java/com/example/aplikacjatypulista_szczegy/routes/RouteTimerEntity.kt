package com.example.aplikacjatypulista_szczegy.routes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_timers", primaryKeys = ["routeId"])
data class RouteTimerEntity(
    val routeId: Long,
    val elapsedSeconds: Long
)
