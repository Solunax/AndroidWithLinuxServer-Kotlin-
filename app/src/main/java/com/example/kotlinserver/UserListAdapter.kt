package com.example.kotlinserver

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class UserListAdapter(var userListContext:Context, var userIdList:ArrayList<String>, var userNameList:ArrayList<String>):BaseAdapter(){
    private var userLayoutInflater:LayoutInflater = LayoutInflater.from(userListContext)

    override fun getCount(): Int {
        return userIdList.size
    }

    override fun getItem(position: Int): Any {
        return userIdList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = userLayoutInflater.inflate(R.layout.list, null)

        var userID = view.findViewById<TextView>(R.id.list_id)
        var userName = view.findViewById<TextView>(R.id.list_name)

        userID.text = "ID : " + userIdList[position]
        userName.text = "NAME : " + userNameList[position]

        return view
    }
}