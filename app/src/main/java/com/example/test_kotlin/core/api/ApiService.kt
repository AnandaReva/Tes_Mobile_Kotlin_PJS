package com.example.test_kotlin.api

import com.example.test_kotlin.model.User
import com.example.test_kotlin.model.UserResponse
import retrofit2.http.GET



interface ApiService {
//    @GET("users")
//    suspend fun getUsers(): List<User>
@GET("users")
suspend fun getUsers(): UserResponse

}

