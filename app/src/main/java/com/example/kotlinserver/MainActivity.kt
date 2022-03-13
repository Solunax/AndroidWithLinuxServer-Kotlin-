package com.example.kotlinserver

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlinserver.Fragment.Dummy
import com.example.kotlinserver.Fragment.Home
import com.example.kotlinserver.Fragment.Setting
import com.example.kotlinserver.ViewPager.HomeViewPagerAdapter
import com.example.kotlinserver.databinding.ActivityMainBinding
import com.example.kotlinserver.model.HomeViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastTimeBackPressed : Long = 0
    private var loginID:String? = null
    private var fragments : ArrayList<Fragment> = ArrayList()
    private var fragmentValue : ArrayList<String>? = ArrayList()
    private val dummy:Dummy = Dummy()
    private val home:Home = Home()
    private val setting:Setting = Setting()
    private var fragmentManager:FragmentManager = supportFragmentManager
    lateinit var homeViewModel:HomeViewModel

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
        binding.homeViewPager.adapter = null
        binding.unbind()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val intent = intent
        loginID = intent.getStringExtra("id")

        val bundle = Bundle()
        bundle.putString("id", loginID)
        setting.arguments = bundle

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        fragments.add(dummy)
        fragments.add(home)
        fragments.add(setting)

        binding.homeViewPager.adapter = HomeViewPagerAdapter(this, fragments)
        binding.homeViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                binding.bottomMenu.menu.getItem(position).isChecked = true

                if(position == 0){
                    fragmentValue = homeViewModel.getViewModelList().value

                    if(fragmentValue != null){
                        val df:Dummy = fragmentManager.findFragmentByTag("f0") as Dummy
                        df.setData(fragmentValue, false)
                    }
                }
            }
        })

        binding.homeViewPager.currentItem = 1

        binding.bottomMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.dummy -> binding.homeViewPager.currentItem = 0
                R.id.main -> binding.homeViewPager.currentItem = 1
                R.id.setting -> binding.homeViewPager.currentItem = 2
            }
            true
        }
    }
}