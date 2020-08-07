package com.project_sk.foodiction.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ResEntity::class],version = 1)
abstract class RestaurantDatabase: RoomDatabase() {

    abstract fun resDao(): ResDao
}