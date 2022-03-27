package com.example.kotlinserver.Fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.kotlinserver.API.ApiClient
import com.example.kotlinserver.MainActivity
import com.example.kotlinserver.R
import com.example.kotlinserver.UpdateUserInformation
import com.example.kotlinserver.UserListAdapter
import com.example.kotlinserver.databinding.SettingFragmentBinding
import com.example.kotlinserver.model.HomeViewModel
import com.example.kotlinserver.model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class Setting: Fragment() {
    private val serverBasePath = "http://192.168.56.117/userImage/"
    private var userIdList : ArrayList<String> = ArrayList()
    private var userNameList : ArrayList<String> = ArrayList()
    private var myInfo : ArrayList<String> = ArrayList()
    private lateinit var homeViewModel:ViewModel
    private lateinit var binding:SettingFragmentBinding
    private lateinit var loginId:String
    private lateinit var job:Job

    private var imageUpload:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data
            val path = getPath(intent!!.data!!)
            uploadImages(loginId, path)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingFragmentBinding.inflate(inflater, container, false)

        val bundle = arguments
        loginId = bundle!!.getString("id").toString()

        loadUserInfo(loginId)

        homeViewModel = ViewModelProvider(activity as MainActivity).get(HomeViewModel::class.java)

        binding.getInfoF.setOnClickListener {
            getUserList()
        }

        binding.profileImageF.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            imageUpload.launch(intent)
        }

        binding.toUpdateUserInfoF.setOnClickListener {
            val intent = Intent(context, UpdateUserInformation::class.java)
            startActivity(intent)
        }

        binding.deleteInfoF.setOnClickListener {
            deleteDialog()
        }

        return binding.root
    }

    private fun loadUserInfo(id:String){
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.getApiClient().getMyInfo(id)
            myInfo = ArrayList()

            response.body()?.getData()?.forEach {
                myInfo.add(it)
            }

            setUserInfo(myInfo)
            requireActivity().runOnUiThread{(homeViewModel as HomeViewModel).setInfoList(myInfo) }
        }
    }

    private fun getUserList(){
        userIdList.clear()
        userNameList.clear()

        job = CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.getApiClient().getUserList()

            response.body()?.data?.forEach {
                userIdList.add(it.id.toString())
                userNameList.add(it.name.toString())
            }

            setAdapter(UserListAdapter(requireContext(), userIdList, userNameList))
        }
    }

    private fun uploadImages(id:String, imageFile:String){
        val file = File(imageFile)
        val serverPath = serverBasePath + id
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body :MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file", id, requestFile)

        job = CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.getApiClient().uploadImage(body, id, serverPath)
            if(response.isSuccessful){
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "업로드에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    Glide.with(requireContext()).load(serverPath).circleCrop().skipMemoryCache(true).diskCacheStrategy(
                        DiskCacheStrategy.NONE).into(binding.profileImageF)}
            }
        }
    }

    private fun setUserInfo(values:ArrayList<String>?){
        val auth = when(values?.get(2)){
            "A" -> "관리자"
            else -> "유저"
        }

        val user = UserModel(values?.get(0).toString(), values?.get(1).toString(), auth)
        binding.user = user

        if(values?.get(3) != null)
            requireActivity().runOnUiThread{Glide.with(requireContext()).load(values[3]).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop().override(100, 100).into(binding.profileImageF)}
        else
            requireActivity().runOnUiThread{Glide.with(requireContext()).load(R.drawable.ic_launcher_background).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop().override(100, 100).into(binding.profileImageF)}
    }

    private fun getPath(uri: Uri):String{
        val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.moveToNext()
        @SuppressLint("Range") val path = cursor!!.getString(cursor.getColumnIndex("_data"))
        cursor.close()

        return path
    }

    private fun setAdapter(adapter:UserListAdapter){
        adapter.notifyDataSetChanged()
        requireActivity().runOnUiThread{binding.infoListF.adapter = adapter}
    }

    private fun deleteDialog(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.delete_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val deleteID:EditText = dialog.findViewById(R.id.deleteUserID)
        val yes: Button = dialog.findViewById(R.id.delete_yes)
        val no: Button = dialog.findViewById(R.id.delete_no)

        yes.setOnClickListener {
            val deleteIdValue:String = deleteID.text.toString().trim()

            if(deleteIdValue.isEmpty()){
                Toast.makeText(requireContext(), "삭제할 아이디를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            job = CoroutineScope(Dispatchers.IO).launch {
                val listResponse = ApiClient.getApiClient().getUserIDs()
                val idList = listResponse.body()?.getData()
                if(idList!!.any { it == deleteIdValue }){
                    val deleteResponse = ApiClient.getApiClient().deleteUser(deleteIdValue)
                    if(deleteResponse.isSuccessful){
                        requireActivity().runOnUiThread {Toast.makeText(requireContext(), "계정을 성공적으로 삭제했습니다.", Toast.LENGTH_SHORT).show()}
                        dialog.dismiss()
                    }
                    else
                        requireActivity().runOnUiThread {Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()}
                }else{
                    requireActivity().runOnUiThread {Toast.makeText(requireContext(), "해당하는 아이디가 없습니다.", Toast.LENGTH_SHORT).show()}
                }



            }
        }

        no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
