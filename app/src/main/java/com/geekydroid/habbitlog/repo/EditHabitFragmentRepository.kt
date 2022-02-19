package com.geekydroid.habbitlog.repo

import com.geekydroid.habbitlog.datasource.MyDatabase
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.entities.HabitAudit

class EditHabitFragmentRepository(
    private val database: MyDatabase
) {


    suspend fun updateHabit(habit: Habit,habitAudit: HabitAudit) {
        database.getHabitDao()?.modifyHabit(habit,habitAudit)
    }
}