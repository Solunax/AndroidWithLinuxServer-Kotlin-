package com.example.kotlinserver

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.kotlinserver.API.ApiClient
import com.example.kotlinserver.databinding.UpdateUserInformationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UpdateUserInformation:Activity() {
    private lateinit var binding:UpdateUserInformationBinding
    private var userIdList:ArrayList<String> = ArrayList()
    private lateinit var job:Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.update_user_information)

        binding.updateUserInfo.setOnClickListener {
            updateUserInfo()
        }
    }

    private fun updateUserInfo(){
        userIdList.clear()
        if(binding.updateUserID.text.toString().isEmpty()){
            Toast.makeText(this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.updateUserName.text.toString().isEmpty()){
            Toast.makeText(this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }

        job = CoroutineScope(Dispatchers.IO).launch {
            val userList = ApiClient.getApiClient().updateUser(binding.updateUserID.text.toString(), binding.updateUserName.text.toString())
            if(userList.isSuccessful)
                runOnUiThread {Toast.makeText(this@UpdateUserInformation, "성공적으로 변경했습니다.", Toast.LENGTH_SHORT).show()}
            else
                runOnUiThread {Toast.makeText(this@UpdateUserInformation, "실패", Toast.LENGTH_SHORT).show()}
        }
    }
}