package com.example.kotlinserver

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import com.example.kotlinserver.API.ApiClient
import com.example.kotlinserver.Room.AppDataBase
import com.example.kotlinserver.Room.User
import com.example.kotlinserver.Room.UserDAO
import com.example.kotlinserver.databinding.LoginActivityBinding
import kotlinx.coroutines.*

class LoginActivity :Activity() {
    private var lastTimeBackPressed : Long = 0
    private var db : AppDataBase? = null
    private var userDAO: UserDAO? = null
    private lateinit var binding : LoginActivityBinding
    private lateinit var job:Job

    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish()
            return
        }
        lastTimeBackPressed = System.currentTimeMillis()
        Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        binding.unbind()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity)

        db = Room.databaseBuilder(this, AppDataBase::class.java, "USER").build()
        userDAO = db!!.userDAO()

        job = CoroutineScope(Dispatchers.IO).launch {
            val data:User = userDAO!!.getUserData()
            try {
                binding.loginId.setText(data.id)
                binding.loginPw.setText(data.password)
                binding.saveLoginInformation.isChecked = data.autoLogin
            }catch (e:Exception){

            }
        }


        binding.login.setOnClickListener {
            val id:String = binding.loginId.text.toString()
            val pw:String = binding.loginPw.text.toString()

            if(checkNull(id)){
                Toast.makeText(this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show()
                binding.loginId.requestFocus()
                return@setOnClickListener
            }

            if(checkNull(pw)){
                Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                binding.loginPw.requestFocus()
                return@setOnClickListener
            }

            tryLogin(id, pw)
        }

        binding.join.setOnClickListener {
            val intent = Intent(this@LoginActivity, JoinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkNull(value:String?) :Boolean{
        return value.isNullOrEmpty()
    }

    private fun tryLogin(id:String, password:String){
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.getApiClient().getLoginInfo(id)

            if(response.body()?.getData().isNullOrEmpty()){
                runOnUiThread{Toast.makeText(this@LoginActivity, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    binding.loginId.requestFocus()}

                return@launch
            }

            if(!(response.body()?.getData()?.get(1).equals(password))){
                runOnUiThread{Toast.makeText(this@LoginActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    binding.loginPw.requestFocus()}

                return@launch
            }

            runOnUiThread{Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()}
            val intent = Intent(this@LoginActivity, MainActivity::class.java)


            val user = User(binding.loginId.text.toString(), binding.loginPw.text.toString(), binding.saveLoginInformation.isChecked)
            userDAO!!.insertUserData(user)

            intent.putExtra("id", id)
            finish()
            startActivity(intent)
        }
    }
}