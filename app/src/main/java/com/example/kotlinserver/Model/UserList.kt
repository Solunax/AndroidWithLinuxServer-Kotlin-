package com.example.kotlinserver.Model

import com.google.gson.annotations.SerializedName

class UserList {
    @SerializedName("data")
    var data:List<Users>? = null

    class Users{
        @SerializedName("id")
        var id:String? = null

        @SerializedName("name")
        var name:String? = null
    }
}