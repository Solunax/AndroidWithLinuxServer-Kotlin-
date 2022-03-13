package com.example.kotlinserver.API

import com.example.kotlinserver.model.Data
import com.example.kotlinserver.model.UserList
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET("getLoginInfo.php")
    suspend fun getLoginInfo(@Query("id")id:String): Response<Data>

    @GET("checkInfo.php")
    suspend fun getUserList():Response<UserList>

    @GET("idCheck.php")
    suspend fun getUserIDs():Response<Data>

    @FormUrlEncoded
    @POST("join.php")
    suspend fun insertUser(@Field("id") id:String, @Field("password") password:String, @Field("name") name:String):Response<UserList>

    @GET("getMyInfo.php")
    suspend fun getMyInfo(@Query("id") id:String):Response<Data>

    @Multipart
    @POST("uploadImage.php")
    suspend fun uploadImage(@Part File:MultipartBody.Part, @Part("id") id:String, @Part("serverPath") serverPath:String): Response<Void>
}