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

// ================= USER LIST SCREEN DENGAN SEARCH BAR + SORT + LOGGING =======================
@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun UserListScreen(users: List<User>, onUserClick: (User) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var sortAsc by remember { mutableStateOf(true) } // true = ascending, false = descending

    // Logging perubahan query
    LaunchedEffect(searchQuery) {
        Logger.debug("UserListScreen", "Search query changed to: '$searchQuery'")
    }

    // Logging perubahan sort order
    LaunchedEffect(sortAsc) {
        Logger.debug("UserListScreen", "Sort order changed to: ${if (sortAsc) "Ascending" else "Descending"}")
    }

    // Filter dan sort users
    val filteredSortedUsers = remember(searchQuery, users, sortAsc) {
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

        val sorted = if (sortAsc) {
            filtered.sortedBy { it.firstName.lowercase() }
        } else {
            filtered.sortedByDescending { it.firstName.lowercase() }
        }

        Logger.debug(
            "UserListScreen",
            "Sorted users by firstName ${if (sortAsc) "ascending" else "descending"}"
        )

        sorted
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Row untuk tombol Sort
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(text = "Sort: ", style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = { sortAsc = !sortAsc },
                modifier = Modifier.height(36.dp)
            ) {
                Text(text = if (sortAsc) "Ascending" else "Descending")
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by first or last name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List of Users
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredSortedUsers) { user ->
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
