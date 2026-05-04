package com.example.aplikacjatypulista_szczegy

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplikacjatypulista_szczegy.routes.RouteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StopwatchUiState(
    val elapsedSeconds: Long = 0L,
    val isRunning: Boolean = false
)

class RouteDetailsViewModel(
    private val repository: RouteRepository,
    private val routeId: Long
) : ViewModel() {

    private val _stopwatchState = MutableStateFlow(StopwatchUiState())
    val stopwatchState: StateFlow<StopwatchUiState> = _stopwatchState.asStateFlow()

    private var elapsedBeforeStartSeconds = 0L
    private var startRealtimeMs = 0L
    private var tickerJob: Job? = null

    init {
        viewModelScope.launch {
            elapsedBeforeStartSeconds = repository.getSavedElapsedSeconds(routeId).first()
            _stopwatchState.update { it.copy(elapsedSeconds = elapsedBeforeStartSeconds) }
        }
    }

    fun onStart() {
        if (_stopwatchState.value.isRunning) return

        startRealtimeMs = SystemClock.elapsedRealtime()
        _stopwatchState.update { it.copy(isRunning = true) }
        startTicker()
    }

    fun onStop() {
        if (!_stopwatchState.value.isRunning) return

        val elapsedNow = (SystemClock.elapsedRealtime() - startRealtimeMs) / 1000
        elapsedBeforeStartSeconds += elapsedNow
        _stopwatchState.update {
            it.copy(
                elapsedSeconds = elapsedBeforeStartSeconds,
                isRunning = false
            )
        }
        stopTicker()
        persistCurrentTime()
    }

    fun onInterrupt() {
        stopTicker()
        elapsedBeforeStartSeconds = 0L
        startRealtimeMs = 0L
        _stopwatchState.update { it.copy(elapsedSeconds = 0L, isRunning = false) }
        persistCurrentTime()
    }

    private fun startTicker() {
        stopTicker()
        tickerJob = viewModelScope.launch {
            while (_stopwatchState.value.isRunning) {
                val runningSeconds = (SystemClock.elapsedRealtime() - startRealtimeMs) / 1000
                _stopwatchState.update {
                    it.copy(elapsedSeconds = elapsedBeforeStartSeconds + runningSeconds)
                }
                delay(250)
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    private fun persistCurrentTime() {
        viewModelScope.launch {
            repository.saveElapsedSeconds(
                routeId = routeId,
                elapsedSeconds = _stopwatchState.value.elapsedSeconds
            )
        }
    }

    override fun onCleared() {
        stopTicker()
        super.onCleared()
    }

    class Factory(
        private val repository: RouteRepository,
        private val routeId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RouteDetailsViewModel::class.java)) {
                return RouteDetailsViewModel(repository, routeId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

