package com.geekydroid.habbitlog.repo

import com.geekydroid.habbitlog.datasource.MyDatabase
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.entities.HabitAudit

class CreateNewHabitRepository(private val database: MyDatabase) {

    suspend fun createNewHabit(habit: Habit, habitAudit: HabitAudit): Long {
        return database.getHabitDao()!!.createNewHabit(habit, habitAudit)
    }


}