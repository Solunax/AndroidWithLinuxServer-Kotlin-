package com.example.kotlinserver.API

import com.example.kotlinserver.Model.Data
import com.example.kotlinserver.Model.UserList
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET("getLoginInfo.php")
    suspend fun getLoginInfo(@Query("id")id:String): Response<Data>

    @GET("idCheck.php")
    suspend fun getUserIDs():Response<Data>

    @FormUrlEncoded
    @POST("join.php")
    suspend fun insertUser(@Field("id") id:String, @Field("password") password:String, @Field("name") name:String):Response<UserList>
}