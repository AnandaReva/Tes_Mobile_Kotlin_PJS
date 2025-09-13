package com.example.test_kotlin.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.test_kotlin.api.ApiService
import com.example.test_kotlin.core.logger.Logger
import com.example.test_kotlin.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserRepository(private val api: ApiService) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchUsers(): Result<List<User>> {
        val methodName = "fetchUsers"
        val className = "UserRepository"

        Logger.debug(className, "$methodName - Memulai proses fetch users")
        Logger.debug(className, "$methodName - Timestamp: ${getCurrentTimestamp()}")

        return try {
            Logger.debug(className, "$methodName - Memanggil API service...")

            val response = api.getUsers()  // UserResponse

            Logger.debug(className, "$methodName - Berhasil menerima response dari API")
            Logger.debug(className, "$methodName - Jumlah users: ${response.users.size}")

            if (response.users.isNotEmpty()) {
                Logger.debug(className, "$methodName - User pertama: ${response.users.first().toString().take(100)}...")
            }
            Result.success(response.users)  // Kembalikan list users dari UserResponse

        } catch (e: Exception) {
            Logger.error(className, "$methodName - Gagal fetch users: ${e.message}", e)
            Logger.debug(className, "$methodName - Exception type: ${e::class.simpleName}")

            Result.failure(e)
        } finally {
            Logger.debug(className, "$methodName - Proses fetch users selesai")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTimestamp(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}