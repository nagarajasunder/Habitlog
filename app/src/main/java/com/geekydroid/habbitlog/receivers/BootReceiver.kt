package com.geekydroid.habbitlog.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.geekydroid.habbitlog.HabitLogApplication
import java.text.DateFormat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Toast.makeText(context!!, "Boot Receiver called", Toast.LENGTH_LONG).show()

        if (intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {

            Toast.makeText(context!!, "Boot Receiver called inside if", Toast.LENGTH_LONG).show()

            println("debug: receiver boot receiver called")
            rescheduleAllAlarms(context!!)
        }

    }

    private fun rescheduleAllAlarms(context: Context) {

        val db = (context.applicationContext as HabitLogApplication).database
        val habitList = db.getHabitDao()?.getAllHabits("")!!.value
        println("debug: receiver $habitList")
        if (!habitList.isNullOrEmpty()) {
            println("debug: receiver inside if $habitList")
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