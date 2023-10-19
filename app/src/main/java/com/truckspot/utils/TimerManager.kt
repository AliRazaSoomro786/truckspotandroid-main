package com.truckspot.utils

import kotlinx.coroutines.*

class TimerManager {
    private var timerJob: Job? = null
    private var elapsedTime = 0L
    private var timerListener: TimerListener? = null

    fun startTimer() {
        timerJob = GlobalScope.launch {
            while (true) {
                delay(1000)
                elapsedTime++
                val formattedTime = getFormattedTime(elapsedTime)
                withContext(Dispatchers.Main) {
                    timerListener?.onTimeUpdate(formattedTime)
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        elapsedTime = 0L
    }

    fun setTimerListener(listener: TimerListener) {
        timerListener = listener
    }

    private fun getFormattedTime(timeInSeconds: Long): String {
        val hours = timeInSeconds / 3600
        val minutes = (timeInSeconds % 3600) / 60
        val seconds = timeInSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}

interface TimerListener {
    fun onTimeUpdate(formattedTime: String)
}
