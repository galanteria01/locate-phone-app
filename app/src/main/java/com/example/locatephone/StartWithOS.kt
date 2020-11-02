package com.example.locatephone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StartWithOS :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action.equals("android.intent.action.BOOT_COMPLETED")){
            val intend = Intent(context,MyService::class.java)
            context!!.startService(intend)
        }
    }

}