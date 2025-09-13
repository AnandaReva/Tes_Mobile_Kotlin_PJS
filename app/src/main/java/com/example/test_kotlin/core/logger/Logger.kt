package com.example.test_kotlin.core.logger

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.test_kotlin.config.AppInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {

    const val ERROR = "ERROR"
    const val WARNING = "WARNING"
    const val EVENT = "EVENT"
    const val INFO = "INFO"
    const val DEBUG = "DEBUG"

    private var logLevel: String = DEBUG

    fun setLogLevel(level: String) {
        logLevel = level
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLogPrefix(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
        val timeStr = now.format(formatter)

        val version = AppInfo.getVersion()
        val appName = AppInfo.getAppName()

        // Ambil stacktrace untuk cari caller function
        val stackTrace = Throwable().stackTrace
        var funcString = ""
        if (stackTrace.size > 3) {
            val element = stackTrace[3]
            funcString = if (logLevel == DEBUG) {
                "${element.className}.${element.methodName}:${element.lineNumber} - "
            } else ""
        }

        return "$timeStr - $appName - VERSION: $version - $funcString"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun debug(id: String, vararg msg: Any?) {
        if (logLevel == DEBUG) {
            println("$id - ${getLogPrefix()}DEBUG - ${msg.joinToString(" ")}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun info(id: String, vararg msg: Any?) {
        if (logLevel in listOf(INFO, DEBUG)) {
            println("$id - ${getLogPrefix()}INFO - ${msg.joinToString(" ")}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun warning(id: String, vararg msg: Any?) {
        if (logLevel in listOf(WARNING, INFO, DEBUG)) {
            println("$id - ${getLogPrefix()}WARNING - ${msg.joinToString(" ")}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun error(id: String, vararg msg: Any?) {
        if (logLevel in listOf(ERROR, WARNING, INFO, DEBUG)) {
            println("$id - ${getLogPrefix()}ERROR - ${msg.joinToString(" ")}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun event(id: String, vararg msg: Any?) {
        if (logLevel in listOf(EVENT, INFO, DEBUG)) {
            println("$id - ${getLogPrefix()}EVENT - ${msg.joinToString(" ")}")
        }
    }
}