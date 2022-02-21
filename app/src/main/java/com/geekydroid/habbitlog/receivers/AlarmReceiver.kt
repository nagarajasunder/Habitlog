package com.geekydroid.habbitlog.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.R
import com.geekydroid.habbitlog.Util
import com.geekydroid.habbitlog.entities.temp
import com.geekydroid.habbitlog.receivers.NotificationReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

private const val TAG = "AlarmReceiver"

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "HABIT") {
            println("debug: receiver called")
            val habitId = intent.getIntExtra("HABIT_ID", 0)

            createNotification(context!!, habitId)

            val database = (context.applicationContext as HabitLogApplication).database
            CoroutineScope(Dispatchers.IO).launch {
                database.getTempDao()
                    ?.insertTemp(
                        temp(
                            tempId = null,
                            insertedOn = System.currentTimeMillis(),
                            habitTime = intent.getStringExtra("TIME_PERIOD")!!
                        )
                    )
            }

            val timePeriod = intent.getStringExtra("TIME_PERIOD")
            Log.d(TAG, "onReceive: Alarm Received for $timePeriod")
            Toast.makeText(context, "The Alarm Just got fired $timePeriod", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun createNotification(context: Context, habitId: Int) {
        Log.d(TAG, "createNotification: $habitId")
        CoroutineScope(IO).launch {
            val habit = (context.applicationContext as HabitLogApplication).database.getHabitDao()
                ?.getHabitById(habitId)

            val doneIntent = Intent(context, NotificationReceiver::class.java)
            doneIntent.apply {
                action = context.getString(R.string.notification_action_yes)
                putExtra("HABIT_ID", habitId)
            }
            val skipIntent = Intent(context, NotificationReceiver::class.java)
            skipIntent.apply {
                action = context.getString(R.string.notification_action_no)
                putExtra("HABIT_ID", habitId)
            }
            val donePendingIntent = PendingIntent.getBroadcast(context, habitId, doneIntent, 0)
            val skipPendingIntent = PendingIntent.getBroadcast(context, habitId, skipIntent, 0)

            val builder =
                NotificationCompat.Builder(context, Util.HABIT_ALARM_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.alarm)
                    .setContentTitle(habit?.habitName)
                    .setContentText(if (habit?.habitQuestion.isNullOrEmpty()) "Did you do this habit today?" else habit?.habitQuestion)
                    .setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .addAction(
                        R.drawable.thumbs_up,
                        context.getString(R.string.notification_action_yes),
                        donePendingIntent
                    )
                    .addAction(
                        R.drawable.thumbs_down,
                        context.getString(R.string.notification_action_no),
                        skipPendingIntent
                    )
                    .setAutoCancel(true)



            with(NotificationManagerCompat.from(context))
            {
                notify(habitId, builder.build())
            }
        }
    }


}