package com.geekydroid.habbitlog.entities

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey

@Entity
data class temp(
    @PrimaryKey(autoGenerate = true)
    var tempId: Int?,
    var habitTime:String,
    var insertedOn: Long
)

@Dao
interface tempDao {
    @Insert
    fun insertTemp(tempItem: temp)
}