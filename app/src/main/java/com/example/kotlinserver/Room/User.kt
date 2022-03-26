package com.example.kotlinserver.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(val id:String, val password:String, val autoLogin:Boolean){
    @PrimaryKey
    var number : Int = 0
}