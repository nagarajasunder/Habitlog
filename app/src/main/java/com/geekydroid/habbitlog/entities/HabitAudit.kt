package com.geekydroid.habbitlog.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HABIT_AUDIT")
data class HabitAudit(
    @PrimaryKey(autoGenerate = true)
    val habitAuditId: Int = 0,
    @Embedded
    val habit: Habit
)