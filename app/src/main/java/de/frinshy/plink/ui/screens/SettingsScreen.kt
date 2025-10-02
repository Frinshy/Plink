package de.frinshy.plink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.frinshy.plink.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository
) {
    val themeMode by settingsRepository.themeMode.collectAsState(initial = de.frinshy.plink.data.ThemeMode.SYSTEM)

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // Title
            Text(text = "Appearance")
            Spacer(modifier = Modifier.height(8.dp))

            // Radio options: System / Light / Dark
            val options = listOf(
                de.frinshy.plink.data.ThemeMode.SYSTEM to "System default",
                de.frinshy.plink.data.ThemeMode.LIGHT to "Light",
                de.frinshy.plink.data.ThemeMode.DARK to "Dark"
            )

            options.forEach { (mode, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = themeMode == mode,
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                settingsRepository.setThemeMode(mode)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = label)
                }
            }
        }
    }
}
