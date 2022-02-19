package com.geekydroid.habbitlog.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.entities.HabitAudit

@Dao
interface habitDao {

    @Insert
    suspend fun insertHabit(habit: Habit): Long

    @Query("SELECT * FROM HABIT WHERE habitName LIKE '%' || :searchString || '%'")
    fun getAllHabits(searchString: String): LiveData<List<Habit>>

    @Update
    suspend fun updateHabit(habit: Habit)


    @Insert
    suspend fun insertHabitAudit(habitAudit: HabitAudit)

    @Transaction
    suspend fun createNewHabit(habit: Habit, habitAudit: HabitAudit): Long {
        val result = insertHabit(habit)
        if (result.toInt() != -1) {
            habitAudit.habit.habitId = result.toInt()
            insertHabitAudit(habitAudit)
        }
        return result
    }

    @Transaction
    suspend fun modifyHabit(habit: Habit, habitAudit: HabitAudit) {
        updateHabit(habit)
        insertHabitAudit(habitAudit)
    }

    @Query("SELECT * FROM HABIT WHERE habitId = :habitId")
    suspend fun getHabitById(habitId: Int): Habit

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Transaction
    suspend fun deleteHabitTx(habit: Habit) {
        deleteHabit(habit)
        deleteHabitAudit(habit.habitId)
    }

    @Query("DELETE FROM HABIT_AUDIT WHERE habitId = :habitId")
    suspend fun deleteHabitAudit(habitId: Int)
}