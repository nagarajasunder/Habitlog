package com.geekydroid.habbitlog.repo

import com.geekydroid.habbitlog.datasource.MyDatabase

class HomeFragmentRepository(private val database: MyDatabase) {

    fun getAllHabits(searchString:String) = database.getHabitDao()!!.getAllHabits(searchString)

}