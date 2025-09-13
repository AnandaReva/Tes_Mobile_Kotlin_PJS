package com.example.test_kotlin.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.test_kotlin.logger.Logger
import com.example.test_kotlin.model.User
import com.example.test_kotlin.viewmodel.UserUiState
import com.example.test_kotlin.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

// =================== MAIN SCREEN - Ambil data dari ViewModel ==============================//
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: UserViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is UserUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UserUiState.Error -> {
            val message = (uiState as UserUiState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: $message", color = MaterialTheme.colorScheme.error)
            }
        }

        is UserUiState.Success -> {
            val users = (uiState as UserUiState.Success).users
            UserListScreen(users = users) { user ->
                Logger.debug("UserListScreen", "Navigating to detail screen for userId=${user.id}")
                navController.navigate("detail/${user.id}")
            }
        }
    }
}

// ================= USER LIST SCREEN DENGAN SEARCH BAR + LOGGING =======================
@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun UserListScreen(users: List<User>, onUserClick: (User) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    // Logging perubahan query
    LaunchedEffect(searchQuery) {
        Logger.debug("UserListScreen", "Search query changed to: '$searchQuery'")
    }

    // 🔍 Filter user berdasarkan nama depan atau belakang
    val filteredUsers = remember(searchQuery, users) {
        val filtered = if (searchQuery.isBlank()) {
            users
        } else {
            users.filter { user ->
                user.firstName.contains(searchQuery, ignoreCase = true) ||
                        user.lastName.contains(searchQuery, ignoreCase = true)
            }
        }

        Logger.debug(
            "UserListScreen",
            "Filtering users with query '$searchQuery'. Result count: ${filtered.size}"
        )

        filtered
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by first or last name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // List of Users
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredUsers) { user ->
                UserItem(user = user) {
                    Logger.debug("UserListScreen", "User item clicked: ${user.firstName} ${user.lastName}")
                    onUserClick(user)
                }
            }
        }
    }
}

// ======================== USER ITEM CARD ==================
@Composable
fun UserItem(user: User, onClick: () -> Unit) {
    val backgroundColor = when {
        user.age <= 30 -> Color(0xFFA5D6A7) // Green
        user.age in 31..40 -> Color(0xFFFFF59D) // Yellow
        else -> Color(0xFFFFCC80) // Orange
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.image,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${user.age} years old",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = user.company.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
