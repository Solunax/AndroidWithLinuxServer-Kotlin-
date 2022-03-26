package com.example.kotlinserver.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun userDAO():UserDAO
}