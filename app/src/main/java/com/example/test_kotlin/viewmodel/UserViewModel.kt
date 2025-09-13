package com.example.test_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_kotlin.logger.Logger
import com.example.test_kotlin.model.User
import com.example.test_kotlin.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val users: List<User>) : UserUiState()
    data class Error(val message: String) : UserUiState()
}

class UserViewModel(private val repo: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            Logger.debug("UserViewModel", "loadUsers - Memulai proses loading users")

            val result = repo.fetchUsers()

            result.onSuccess { users ->
                Logger.debug("UserViewModel", "loadUsers - Sukses: ${users.size} users loaded")
                _uiState.value = UserUiState.Success(users)
            }.onFailure { exception ->
                Logger.error("UserViewModel", "loadUsers - Error: ${exception.message}", exception)
                _uiState.value = UserUiState.Error(exception.message ?: "Unknown error occurred")
            }

            Logger.debug("UserViewModel", "loadUsers - Proses selesai")
        }
    }
}