package com.example.testimage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBrocast : BroadcastReceiver() {
    val ACTION = "android.intent.action.BOOT_COMPLETED"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v("dimos", "MyBrocast")
        if (intent!!.action.equals(ACTION)) {
            val service = Intent(context, MyService::class.java)
            context!!.startService(service)
        }
    }
}