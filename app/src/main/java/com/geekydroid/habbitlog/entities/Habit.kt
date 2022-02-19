package com.geekydroid.habbitlog.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "HABIT")
@Parcelize
data class Habit(
    @PrimaryKey(autoGenerate = true)
    var habitId: Int = 0,
    var habitName: String,
    var habitHour: Int,
    var habitMin: Int,
    var alarmTime: Long,
    val createdOn: Long = System.currentTimeMillis(),
    var habitQuestion: String,
    var habitNotes: String,
    var updatedOn: Long
) : Parcelable {

    val createdOnFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdOn)

    val updatedOnFormatted: String
        get() = DateFormat.getDateTimeInstance().format(updatedOn)
}