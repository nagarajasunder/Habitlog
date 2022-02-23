package com.geekydroid.habbitlog.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.entities.HabitAudit
import com.geekydroid.habbitlog.receivers.AlarmReceiver
import com.geekydroid.habbitlog.repo.EditHabitFragmentRepository
import kotlinx.coroutines.launch
import java.text.DateFormat

class EditHabitFragmentViewModel(
    private val repo: EditHabitFragmentRepository,
    private val application: HabitLogApplication
) : ViewModel() {


    fun updateHabit(oldHabit: Habit, habit: Habit, habitAudit: HabitAudit) {
        viewModelScope.launch {
            repo.updateHabit(habit, habitAudit)
            rescheduleAlarm(newHabit = habit, oldHabit = oldHabit)
        }
    }

    fun rescheduleAlarm(oldHabit: Habit, newHabit: Habit) {

        if (oldHabit.alarmTime != newHabit.alarmTime) {
            val alarmIntent = Intent(application.applicationContext, AlarmReceiver::class.java)
            alarmIntent.action = "HABIT"
            alarmIntent.putExtra("HABIT_ID", newHabit.habitId)
            val alarmPendingIntent = PendingIntent.getBroadcast(
                application.applicationContext,
                newHabit.habitId,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = application.alarmManager
            alarmManager.cancel(alarmPendingIntent)
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                newHabit.alarmTime,
                AlarmManager.INTERVAL_DAY,
                alarmPendingIntent
            )
        }
    }
}

class EditHabitFragmentViewModelFactory(
    private val repo: EditHabitFragmentRepository,
    private val application: HabitLogApplication
) :
    ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            EditHabitFragmentRepository::class.java,
            HabitLogApplication::class.java
        ).newInstance(repo, application)
    }

}