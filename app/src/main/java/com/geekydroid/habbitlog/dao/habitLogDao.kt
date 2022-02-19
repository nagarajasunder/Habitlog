package com.geekydroid.habbitlog.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.geekydroid.habbitlog.entities.HabitLog
import java.util.*

@Dao
interface habitLogDao {

    @Insert
    suspend fun insertHabitLog(habitLog: HabitLog)

    @Update
    suspend fun updateHabitLog(habitLog: HabitLog)

    @Query("SELECT * FROM HABIT_LOG WHERE habitMasterId = :habitId AND actionDate = :actionDate")
    suspend fun getHabitLogByDate(habitId: Int, actionDate: Date): HabitLog?

    @Query("SELECT * FROM HABIT_LOG WHERE habitMasterId = :habitId ORDER by actionDate ASC")
    fun getHabitLogDates(habitId: Int): LiveData<List<HabitLog>>

    @Query("SELECT * FROM HABIT_LOG WHERE habitMasterId = :habitId")
    suspend fun getAllHabitLogs(habitId: Int): List<HabitLog>

    @Query("SELECT  strftime('%m',actionDateStr) as month , COUNT(habitMasterId) as count FROM HABIT_LOG GROUP BY strftime('%m',actionDateStr)")
    suspend fun getHabitLogCountBasedOnMonth(): List<MonthLogCount>

}

data class MonthLogCount(var month:String,var count:Int)
