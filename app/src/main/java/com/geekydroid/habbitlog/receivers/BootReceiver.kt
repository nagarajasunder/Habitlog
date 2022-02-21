package com.geekydroid.habbitlog.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.geekydroid.habbitlog.HabitLogApplication
import java.text.DateFormat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            println("debug: receiver boot receiver called")
            rescheduleAllAlarms(context!!)
        }

    }

    private fun rescheduleAllAlarms(context: Context) {
        val db = (context.applicationContext as HabitLogApplication).database
        val habitList = db.getHabitDao()?.getAllHabits("")?.value
        if (!habitList.isNullOrEmpty()) {
            println("debug: receiver $habitList")
            for (currentHabit in habitList) {
                val intent = Intent(context, AlarmReceiver::class.java)
                intent.putExtra("HABIT_ID", currentHabit.habitId)
                intent.action = "HABIT"
                intent.putExtra(
                    "TIME_PERIOD",
                    DateFormat.getDateTimeInstance().format(currentHabit.alarmTime)
                )
                val alarmIntent = PendingIntent.getBroadcast(
                    context,
                    currentHabit.habitId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
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