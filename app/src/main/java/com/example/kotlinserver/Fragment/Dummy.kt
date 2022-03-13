package com.example.kotlinserver.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinserver.databinding.DummyFragmentBinding
import com.example.kotlinserver.model.HomeViewModel
import com.example.kotlinserver.model.UserModel

class Dummy :Fragment() {
    private var fragmentValue:ArrayList<String>? = ArrayList()
    private lateinit var binding:DummyFragmentBinding

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DummyFragmentBinding.inflate(inflater, container, false)

        var homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        fragmentValue = homeViewModel.getViewModelList().value

        setData(fragmentValue, true)

        return binding.root
    }

    fun setData(data:ArrayList<String>?, isCallInFragment:Boolean){
        if(isCallInFragment){
            if (fragmentValue != null){
                var user = UserModel(fragmentValue!![0], fragmentValue!![1], fragmentValue!![2])
                binding.user = user
            }else{
                Toast.makeText(context, "로딩된 회원 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }else{
            if(data != null){
                var user = UserModel(data[0], data[1], data[2])
                binding.user = user
            }else{
                Toast.makeText(context, "로딩된 회원 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}