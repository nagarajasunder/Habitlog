package com.geekydroid.habbitlog.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.entities.Habit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    private lateinit var habitList: List<Habit>

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            CoroutineScope(IO).launch {
                getAllHabits(context!!)
                rescheduleAllAlarms(context)
            }
        }

    }

    private suspend fun getAllHabits(context: Context) {
        val db = (context.applicationContext as HabitLogApplication).database
        habitList = db.getHabitDao()?.getAllHabitsForReschedule()!!
    }

    private suspend fun rescheduleAllAlarms(context: Context) {
        if (!habitList.isNullOrEmpty()) {
            for (currentHabit in habitList) {
                val intent = Intent(context, AlarmReceiver::class.java)
                intent.putExtra("HABIT_ID", currentHabit.habitId)
                intent.action = "HABIT"
                val alarmIntent = PendingIntent.getBroadcast(
                    context,
                    currentHabit.habitId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmManager = (context.applicationContext as HabitLogApplication).alarmManager
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    currentHabit.alarmTime,
                    alarmIntent
                )
            }
        }

    }
}