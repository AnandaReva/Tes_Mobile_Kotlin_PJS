package com.example.test_kotlin.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.test_kotlin.logger.Logger
import com.example.test_kotlin.viewmodel.UserUiState
import com.example.test_kotlin.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun DetailScreen(
    userId: Int,
    viewModel: UserViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Logger.debug("DetailScreen", "Opening detail for userId = $userId")

    if (state is UserUiState.Success) {
        val user = (state as UserUiState.Success).users.find { it.id == userId }

        user?.let {
            Logger.debug("DetailScreen", "User found: ${it.firstName} ${it.lastName}")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // ==================== Foto + Nama + Jabatan ===================== //
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = it.image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "${it.firstName} ${it.lastName}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "${it.company.title} at ${it.company.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 📝 Info detail
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("👤 Age: ${it.age}", style = MaterialTheme.typography.bodyMedium)
                    Text("🎂 Birth Date: ${it.birthDate}", style = MaterialTheme.typography.bodyMedium)
                    Text("📞 Phone: ${it.phone}", style = MaterialTheme.typography.bodyMedium)
                    Text("📧 Email: ${it.email}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } ?: run {
            Logger.warning("DetailScreen", "User with ID $userId not found")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("User not found", style = MaterialTheme.typography.bodyLarge)
            }
        }
    } else {
        Logger.info("DetailScreen", "UI state is not success, waiting or failed")
    }
}
