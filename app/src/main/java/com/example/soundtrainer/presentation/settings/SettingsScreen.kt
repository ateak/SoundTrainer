package com.example.soundtrainer.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soundtrainer.data.GameSettings
import com.example.soundtrainer.presentation.background.StarsBackground

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    var selectedDifficulty by remember { mutableStateOf(settingsViewModel.getCurrentDifficulty()) }

    Box(modifier = Modifier.fillMaxSize()) {
        StarsBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Настройки",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Уровень сложности",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DifficultyOption(
                    "Легкий",
                    selectedDifficulty == GameSettings.Difficulty.EASY
                ) {
                    selectedDifficulty = GameSettings.Difficulty.EASY
                    settingsViewModel.setDifficulty(GameSettings.Difficulty.EASY)

                }
                DifficultyOption(
                    "Средний",
                    selectedDifficulty == GameSettings.Difficulty.MEDIUM
                ) {
                    selectedDifficulty = GameSettings.Difficulty.MEDIUM
                    settingsViewModel.setDifficulty(GameSettings.Difficulty.MEDIUM)

                }
                DifficultyOption(
                    "Сложный",
                    selectedDifficulty == GameSettings.Difficulty.HARD
                ) {
                    selectedDifficulty = GameSettings.Difficulty.HARD
                    settingsViewModel.setDifficulty(GameSettings.Difficulty.HARD)
                }
            }
        }
    }
}

@Composable
private fun DifficultyOption(
    difficulty: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        )
        Text(
            text = difficulty,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}