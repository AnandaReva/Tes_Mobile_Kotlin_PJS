package com.example.test_kotlin.module

import com.example.test_kotlin.api.ApiService // Make sure to import your ApiService
import com.example.test_kotlin.repository.UserRepository // Assuming this is the correct import
import com.example.test_kotlin.viewmodel.UserViewModel // Assuming this is the correct import
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import org.koin.androidx.viewmodel.dsl.viewModel


val appModule = module {
    single<ApiService> {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    single { UserRepository(get()) }

    viewModel { UserViewModel(get()) }
}
