package de.fitapp.app.util

import de.fitapp.app.data.entity.ExerciseLogEntity
import de.fitapp.app.data.entity.MuscleGroup
import java.time.LocalDate
import kotlin.math.min

/** Zeitraum, der in die Fortschrittsauswertung pro Muskelgruppe einfließt. */
const val PROGRESS_WINDOW_DAYS = 90L

/** Mindestanzahl protokollierter Sätze, ab der eine Muskelgruppe überhaupt bewertet wird. */
private const val MIN_SETS_FOR_DATA = 3

data class MuscleProgressResult(
    val group: MuscleGroup,
    val hasEnoughData: Boolean,
    val progressPercent: Int, // 0..100, nur gültig wenn hasEnoughData
    val totalSets: Int,
    val estimatedOneRepMaxKg: Float
)

/**
 * Berechnet für jede Muskelgruppe einen nachvollziehbaren Fortschrittswert:
 * 1. Für jede Übung wird ein geschätztes 1-Wiederholungs-Maximum (Epley-Formel) bestimmt.
 * 2. Es wird verglichen, wie sich dieser Schätzwert vom ersten zum letzten Log im Zeitfenster
 *    entwickelt hat (relative Steigerung), begrenzt auf 0..100%.
 * 3. Ohne ausreichende Datenlage (siehe MIN_SETS_FOR_DATA) gilt die Gruppe als "keine Daten"
 *    statt fälschlich mit 0% bewertet zu werden.
 */
object MuscleProgressCalculator {

    fun calculate(logs: List<ExerciseLogEntity>, today: LocalDate = LocalDate.now()): List<MuscleProgressResult> {
        val sinceEpochDay = today.minusDays(PROGRESS_WINDOW_DAYS).toEpochDay()

        return MuscleGroup.values().map { group ->
            val groupLogs = logs.filter { it.muscleGroup == group && it.dateEpochDay >= sinceEpochDay }
                .sortedBy { it.dateEpochDay }

            val totalSets = groupLogs.sumOf { it.sets }

            if (groupLogs.isEmpty() || totalSets < MIN_SETS_FOR_DATA) {
                return@map MuscleProgressResult(
                    group = group,
                    hasEnoughData = false,
                    progressPercent = 0,
                    totalSets = totalSets,
                    estimatedOneRepMaxKg = 0f
                )
            }

            val oneRms = groupLogs.map { estimateOneRepMax(it.weightKg, it.reps) }
            val first = oneRms.first()
            val last = oneRms.last()
            val best = oneRms.max()

            // Relative Verbesserung vom ersten zum letzten Wert im Zeitfenster.
            val relativeGain = if (first > 0f) ((last - first) / first) * 100f else 0f
            // Auf 0..100 begrenzen: 0% = keine/negative Veränderung, 100% = +50% oder mehr Steigerung.
            val progress = ((relativeGain / 50f) * 100f).coerceIn(0f, 100f)

            MuscleProgressResult(
                group = group,
                hasEnoughData = true,
                progressPercent = progress.toInt(),
                totalSets = totalSets,
                estimatedOneRepMaxKg = best
            )
        }
    }

    /** Epley-Formel zur Schätzung des 1-Wiederholungs-Maximums. */
    private fun estimateOneRepMax(weightKg: Float, reps: Int): Float {
        if (reps <= 0) return weightKg
        return weightKg * (1f + min(reps, 20) / 30f)
    }
}
