package com.geekydroid.habbitlog.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "HABIT_LOG",
    foreignKeys = [ForeignKey(
        entity = Habit::class,
        parentColumns = ["habitId"],
        childColumns = ["habitMasterId"],
        onDelete = CASCADE
    )]
)
data class HabitLog(
    @PrimaryKey(autoGenerate = true)
    val habitLogId: Int = 0,
    val habitMasterId: Int,
    val actionDate: Date? = null,
    val actionDateStr: String,
    var actionType: String,
    var updatedOn: Long = System.currentTimeMillis()
)