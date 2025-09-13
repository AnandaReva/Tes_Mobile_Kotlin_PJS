package com.example.test_kotlin.model


data class UserResponse(
    val users: List<User>
)

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val email: String,
    val phone: String,
    val birthDate: String,
    val image: String,
    val company: Company
)

data class Company(
    val name: String,
    val title: String
)