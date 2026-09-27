package de.fitapp.app.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.fitapp.app.data.entity.MuscleGroup
import de.fitapp.app.ui.AppViewModel
import de.fitapp.app.util.PROGRESS_WINDOW_DAYS
import java.time.LocalDate

private fun MuscleGroup.label() = when (this) {
    MuscleGroup.BRUST -> "Brust"
    MuscleGroup.BEINE -> "Beine"
    MuscleGroup.BAUCH -> "Bauch"
    MuscleGroup.ARME -> "Arme"
    MuscleGroup.RUECKEN -> "Rücken"
    MuscleGroup.SCHULTERN -> "Schultern"
}

@Composable
fun ProgressScreen(viewModel: AppViewModel) {
    val muscleProgress by viewModel.muscleProgress.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val weightEntries by viewModel.weightEntries.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Fortschritt") }) }) { padding ->
        LazyColumn(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Muskelgruppen-Fortschritt", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Basiert auf deinen protokollierten Trainingssätzen der letzten $PROGRESS_WINDOW_DAYS Tage " +
                                "(geschätztes 1-Wiederholungs-Maximum, Veränderung vom ersten zum letzten Eintrag). " +
                                "Gruppen mit zu wenigen Einträgen zeigen „noch keine Daten“.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(8.dp))
                        if (muscleProgress.isNotEmpty()) {
                            MuscleRadarChart(muscleProgress) { it.label() }
                        }
                        Spacer(Modifier.height(8.dp))
                        muscleProgress.forEach { r ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(r.group.label())
                                Text(if (r.hasEnoughData) "${r.progressPercent}% (${r.totalSets} Sätze)" else "noch keine Daten")
                            }
                        }
                    }
                }
            }

            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Kalorienziel-Streak", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            StreakStat("Aktuell", streak.currentStreak)
                            StreakStat("Rekord", streak.longestStreak)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Letzte 28 Tage:", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        StreakCalendarGrid(streak.achievedDays)
                        Text(
                            "Tage ohne Eintrag zählen nicht automatisch als erreicht.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Gewichtsverlauf", style = MaterialTheme.typography.titleMedium)
                        if (weightEntries.isEmpty()) {
                            Text("Noch keine Gewichtseinträge. Du kannst dein Gewicht im Profil aktualisieren.")
                        } else {
                            weightEntries.take(10).forEach { w ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(LocalDate.ofEpochDay(w.dateEpochDay).toString())
                                    Text("${w.weightKg} kg")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakStat(label: String, value: Int) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text("$value", style = MaterialTheme.typography.headlineMedium)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun StreakCalendarGrid(achievedDays: Set<Long>) {
    val today = LocalDate.now().toEpochDay()
    val days = (0..27).map { today - (27 - it) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(140.dp)
    ) {
        items(days) { day ->
            val achieved = day in achievedDays
            Box(
                Modifier
                    .padding(2.dp)
                    .size(16.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(
                        if (achieved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}
