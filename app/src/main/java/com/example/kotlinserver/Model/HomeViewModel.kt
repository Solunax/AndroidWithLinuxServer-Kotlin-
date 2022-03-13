package com.example.kotlinserver.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel:ViewModel() {
    private var infoList:MutableLiveData<ArrayList<String>> = MutableLiveData()

    fun getViewModelList():LiveData<ArrayList<String>>{
        return infoList
    }

    fun setInfoList(values:ArrayList<String>){
        infoList.value = values
    }
}