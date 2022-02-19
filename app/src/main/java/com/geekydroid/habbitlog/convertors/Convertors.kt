package com.geekydroid.habbitlog.convertors

import androidx.room.TypeConverter
import java.util.*

class Convertors {

    @TypeConverter
    fun fromTimeStamp(timeStamp: Long?): Date? {

        return timeStamp?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun toTimeStamp(date: Date?): Long? {
        return date?.time
    }
}