package com.example.kotlinserver.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface UserDAO {
    @Insert(onConflict = REPLACE)
    suspend fun insertUserData(user:User)

    @Query("SELECT * FROM user")
    suspend fun getUserData(): User
}