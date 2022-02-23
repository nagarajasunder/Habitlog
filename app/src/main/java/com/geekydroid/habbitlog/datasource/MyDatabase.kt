package com.geekydroid.habbitlog.datasource

import android.app.Application
import androidx.room.*
import com.geekydroid.habbitlog.convertors.Convertors
import com.geekydroid.habbitlog.dao.habitDao
import com.geekydroid.habbitlog.dao.habitLogDao
import com.geekydroid.habbitlog.entities.*


@Database(
    entities = [Habit::class, HabitAudit::class,  HabitLog::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Convertors::class)
abstract class MyDatabase : RoomDatabase() {

    abstract fun getHabitDao(): habitDao?
    abstract fun getHabitLogDao(): habitLogDao?

    companion object {

        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getInstance(application: Application): MyDatabase {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    application.applicationContext,
                    MyDatabase::class.java,
                    "habitlog.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}