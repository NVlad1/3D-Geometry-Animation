package io.github.nvlad1.function3danimator.ui

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.nvlad1.function3danimator.database.FunctionRepository
import io.github.nvlad1.function3danimator.model.DefaultFunctionSet
import io.github.nvlad1.function3danimator.openGLutils.FunctionRenderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val functionRepository: FunctionRepository
) : ViewModel() {
    var uiState by mutableStateOf(MainUiState())
        private set

    private var timerJob: Job? = null
    private val animationClock = PausableAnimationClock()
    private var initialized = false

    fun onResume() {
        viewModelScope.launch(Dispatchers.Main) {
            functionRepository.initComplete.first { it }
            functionRepository.filterFunctions()
            if (!initialized) {
                animationClock.reset()
                initialized = true
            }
            animationClock.resume()
            refreshRenderState()
            startTimer()
        }
    }

    fun onPause() {
        animationClock.pause()
        stopTimer()
    }

    fun selectExample(set: DefaultFunctionSet) {
        functionRepository.clear()
        for (function in set.functionList) {
            functionRepository.addFunction(function.copy())
        }
        animationClock.reset()
        refreshRenderState()
        startTimer()
    }

    fun resetAnimationTime() {
        animationClock.reset()
        uiState = uiState.copy(timerText = buildTimerText())
    }

    private fun refreshRenderState() {
        uiState = uiState.copy(
            renderState = FunctionRenderState(
                functions = functionRepository.getFunctions(),
                border = functionRepository.getBorder(),
                elapsedTimeMs = animationClock::elapsedMs
            ),
            renderVersion = uiState.renderVersion + 1
        )
    }

    private fun startTimer() {
        stopTimer()
        if (functionRepository.anyFunctionTimeDependent()) {
            uiState = uiState.copy(
                showTimer = true,
                timerText = buildTimerText()
            )
            timerJob = viewModelScope.launch(Dispatchers.Main) {
                while (isActive) {
                    uiState = uiState.copy(timerText = buildTimerText())
                    delay(100L)
                }
            }
        } else {
            uiState = uiState.copy(
                showTimer = false,
                timerText = ""
            )
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun buildTimerText(): String {
        val time = animationClock.elapsedMs().toFloat() / 1000f
        return "t = ${String.format(Locale.US, "%.1f", time)} s"
    }

    override fun onCleared() {
        stopTimer()
        super.onCleared()
    }
}

private class PausableAnimationClock {
    @Volatile
    private var elapsedBeforePauseMs = 0L

    @Volatile
    private var runningStartedAtMs = 0L

    @Volatile
    private var running = false

    @Synchronized
    fun resume() {
        if (running) return
        runningStartedAtMs = SystemClock.elapsedRealtime()
        running = true
    }

    @Synchronized
    fun pause() {
        if (!running) return
        elapsedBeforePauseMs = elapsedMsLocked()
        running = false
    }

    @Synchronized
    fun reset() {
        elapsedBeforePauseMs = 0L
        if (running) {
            runningStartedAtMs = SystemClock.elapsedRealtime()
        }
    }

    fun elapsedMs(): Long {
        return synchronized(this) {
            elapsedMsLocked()
        }
    }

    private fun elapsedMsLocked(): Long {
        return if (running) {
            elapsedBeforePauseMs + SystemClock.elapsedRealtime() - runningStartedAtMs
        } else {
            elapsedBeforePauseMs
        }
    }
}

data class MainUiState(
    val renderState: FunctionRenderState? = null,
    val renderVersion: Int = 0,
    val showTimer: Boolean = false,
    val timerText: String = ""
)
