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
import com.geekydroid.habbitlog.repo.CreateNewHabitRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.DateFormat

class CreateNewHabitViewModel(
    private val repo: CreateNewHabitRepository,
    private val application: HabitLogApplication
) : ViewModel() {


    fun createNewHabit(habit: Habit, habitAudit: HabitAudit) {

        viewModelScope.launch {
            habit.habitId = async {
                repo.createNewHabit(habit, habitAudit)
            }.await().toInt()
            scheduleAlarm(habit)


        }
    }

    private fun scheduleAlarm(habit: Habit) {
        val alarmManager = application.alarmManager
        val intent = Intent(application.applicationContext, AlarmReceiver::class.java)
        intent.action = "HABIT"
        intent.putExtra("TIME_PERIOD", DateFormat.getDateTimeInstance().format(habit.alarmTime))
        intent.putExtra("HABIT_ID", habit.habitId)
        val alarmPendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,
            habit.habitId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            habit.alarmTime,
//            AlarmManager.INTERVAL_DAY,
            alarmPendingIntent
        )

    }

}


class CreateNewHabitViewModelFactory(
    private val repo: CreateNewHabitRepository,
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
            CreateNewHabitRepository::class.java,
            HabitLogApplication::class.java
        ).newInstance(repo, application)
    }

}
