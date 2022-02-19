package com.geekydroid.habbitlog

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.geekydroid.habbitlog.datasource.MyDatabase
import com.geekydroid.habbitlog.repo.CreateNewHabitRepository
import com.geekydroid.habbitlog.repo.EditHabitFragmentRepository
import com.geekydroid.habbitlog.repo.HomeFragmentRepository
import com.geekydroid.habbitlog.repo.ViewHabitFragmentRepository


class HabitLogApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        val channelId = Util.HABIT_ALARM_NOTIFICATION_CHANNEL_ID
        val channelName = resources.getString(R.string.habit_alarm_notification_name)
        val channelDescription = resources.getString(R.string.habit_alarm_notification_description)
        val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel =
            NotificationChannel(channelId, channelName, channelImportance).apply {
                description = channelDescription
            }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(notificationChannel)
    }

    val database: MyDatabase by lazy {
        MyDatabase.getInstance(this)
    }

    val createNewhabitRepo: CreateNewHabitRepository by lazy {
        CreateNewHabitRepository(database)
    }

    val homeFragmentRepo: HomeFragmentRepository by lazy {
        HomeFragmentRepository(database)
    }


    val editHabitFragmentRepo: EditHabitFragmentRepository by lazy {
        EditHabitFragmentRepository(database)
    }

    val alarmManager: AlarmManager by lazy {
        getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    val viewHabitFragmentRepo: ViewHabitFragmentRepository by lazy {
        ViewHabitFragmentRepository(database)
    }

}