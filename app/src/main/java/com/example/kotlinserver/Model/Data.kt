package com.example.kotlinserver.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("data")
    @Expose
    private var data:List<String>? = null

    fun getData():List<String>?{
        return data
    }

    fun setData(data:List<String>){
        this.data = data
    }
}