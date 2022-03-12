package com.example.kotlinserver

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.kotlinserver.API.ApiClient
import com.example.kotlinserver.databinding.JoinActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class JoinActivity: Activity() {
    private var idList:List<String>? = null
    private lateinit var binding: JoinActivityBinding
    private lateinit var job:Job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.join_activity)

        binding.idCheck.setOnClickListener {
            if(binding.joinUserID.text.toString().isEmpty()){
                Toast.makeText(this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show()
                binding.joinUserID.requestFocus()
                return@setOnClickListener
            }

            idChecking(binding.joinUserID.text.toString())
        }

        binding.joinComplete.setOnClickListener {
            val id = binding.joinUserID.text.toString()
            val password = binding.joinUserPW.text.toString()
            val name = binding.joinUserName.text.toString()

            if(password.isEmpty()){
                Toast.makeText(applicationContext, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                binding.joinUserPW.requestFocus()
                return@setOnClickListener
            }

            if(name.isEmpty()){
                Toast.makeText(applicationContext, "이름을 입력하세요", Toast.LENGTH_SHORT).show()
                binding.joinUserName.requestFocus()
                return@setOnClickListener
            }

            joinUser(id, password, name)
        }
    }

    private fun idChecking(id:String?){
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.getApiClient().getUserIDs()

            if(response.body()?.getData().isNullOrEmpty()){
                runOnUiThread{Toast.makeText(this@JoinActivity, "오류 발생", Toast.LENGTH_SHORT).show()}
                return@launch
            }

            idList = response.body()?.getData()

            if(idList!!.contains(id)){
                runOnUiThread{Toast.makeText(this@JoinActivity, "중복된 아이디가 존재합니다.", Toast.LENGTH_SHORT).show()}
            }else{
                runOnUiThread{Toast.makeText(this@JoinActivity, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
                    binding.idCheck.isEnabled = false
                    binding.joinUserID.isEnabled = false
                    binding.joinComplete.isEnabled = true
                }
            }
        }
    }

    private fun joinUser(id:String, pw:String, name:String){
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.getApiClient().insertUser(id, pw, name)

            if(response.isSuccessful){
                runOnUiThread{Toast.makeText(this@JoinActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }else{
                runOnUiThread{Toast.makeText(this@JoinActivity, "회원가입 실패", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}