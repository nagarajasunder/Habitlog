package com.geekydroid.habbitlog.repo

import androidx.lifecycle.LiveData
import com.geekydroid.habbitlog.datasource.MyDatabase
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.entities.HabitLog
import java.util.*


class ViewHabitFragmentRepository(private var database: MyDatabase) {

    suspend fun insertHabitLog(habitLog: HabitLog) {
        database.getHabitLogDao()?.insertHabitLog(habitLog)
    }

    suspend fun updateHabitLog(habitLog: HabitLog) {
        database.getHabitLogDao()?.updateHabitLog(habitLog)
    }

    suspend fun getHabitLobByDate(habitId: Int, actionDate: Date): HabitLog? {
        return database.getHabitLogDao()?.getHabitLogByDate(habitId, actionDate)
    }

    fun getHabitLogDates(habitId: Int): LiveData<List<HabitLog>> {
        return database.getHabitLogDao()!!.getHabitLogDates(habitId)
    }

    suspend fun deleteHabit(habit: Habit) {
        database.getHabitDao()!!.deleteHabitTx(habit)
    }

    suspend fun getAllHabitLogs(habitId: Int)  = database.getHabitLogDao()?.getAllHabitLogs(habitId)
    suspend fun getLogCountByMonth() = database.getHabitLogDao()?.getHabitLogCountBasedOnMonth()


}