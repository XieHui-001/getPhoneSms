package com.example.testimage

import android.content.Intent
import android.telephony.SmsMessage


class SmsHelper {
    companion object{
        /**
         * 获得短信内容
         */
        fun getSmsBody(intent: Intent): String? {
            var tempString = ""
            val bundle = intent.extras
            val messages = bundle!!["pdus"] as Array<Any>?
            val smsMessage: Array<SmsMessage?> = arrayOfNulls<SmsMessage>(messages!!.size)
            for (n in messages!!.indices) {
                smsMessage[n] = SmsMessage.createFromPdu(messages[n] as ByteArray)
                // 短信有可能因为使用了回车而导致分为多条，所以要加起来接受
                tempString += smsMessage[n]!!.displayMessageBody
            }
            return tempString
        }

        /**
         * 获得短信地址
         */
        fun getSmsAddress(intent: Intent): String? {
            val bundle = intent.extras
            val messages = bundle!!["pdus"] as Array<Any>?
            return SmsMessage.createFromPdu(messages!![0] as ByteArray)
                .displayOriginatingAddress
        }
    }
}