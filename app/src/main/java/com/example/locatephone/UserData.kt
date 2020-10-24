package com.example.locatephone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class UserData {
    var context:Context?=null
    var sRef:SharedPreferences?=null
    constructor(context:Context){
        this.context = context
        this.sRef = context.getSharedPreferences("userData",Context.MODE_PRIVATE)
    }

    fun saveNumber(number:String){
        var editor = sRef!!.edit()
        editor.putString("phoneNumber",number)
        editor.commit()
    }

    fun loadPhoneNumber():String{
        val phoneNumber = sRef!!.getString("phoneNumber","empty")
        if(phoneNumber == "empty"){
            val intent = Intent(context,LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(intent)

        }
        return phoneNumber.toString()
    }

}