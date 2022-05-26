package com.example.testimage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testimage.databinding.ActivityMainBinding
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val intentMy = intent.setClass(this@MainActivity, MyService::class.java)
    companion object {
        lateinit var binding: ActivityMainBinding
        const val REQUEST_CODE_ASK_PERMISSIONS = 123
        lateinit var instance : MainActivity
        @JvmStatic
        fun startActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            if (context is Application) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            instance = this@MainActivity
            getPermission()
            btnGetAllSms.setOnClickListener { startService(intentMy) }

            btnStopService.setOnClickListener { stopService(intentMy) }
        }
    }

    private fun getPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val hasReadSmsPermission = checkSelfPermission(Manifest.permission.READ_SMS)
            if (hasReadSmsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_SMS), REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }


    @SuppressLint("LongLogTag")
    fun getSmsInPhone(): String? {
        val SMS_URI_ALL = "content://sms/" // 所有短信
        val SMS_URI_INBOX = "content://sms/inbox" // 收件箱
        val SMS_URI_SEND = "content://sms/sent" // 已发送
        val SMS_URI_DRAFT = "content://sms/draft" // 草稿
        val SMS_URI_OUTBOX = "content://sms/outbox" // 发件箱
        val SMS_URI_FAILED = "content://sms/failed" // 发送失败
        val SMS_URI_QUEUED = "content://sms/queued" // 待发送列表
        val smsBuilder = StringBuilder()
        try {
            val uri: Uri = Uri.parse(SMS_URI_ALL)
            val projection = arrayOf(
                "_id", "address", "person",
                "body", "date", "type"
            )
//            var cur: Cursor? = contentResolver.query(
//                uri, projection, null,
//                null, "date desc"
//            ) // 获取手机内部短信
            // 获取短信中最新的未读短信
            var cur = contentResolver.query(uri, projection,
                "read = ?", arrayOf("0"), "date desc")
            if (cur!!.moveToFirst()) {
                val index_Address: Int = cur.getColumnIndex("address")
                val index_Person: Int = cur.getColumnIndex("person")
                val index_Body: Int = cur.getColumnIndex("body")
                val index_Date: Int = cur.getColumnIndex("date")
                val index_Type: Int = cur.getColumnIndex("type")
                do {
                    val strAddress: String = cur.getString(index_Address)
                    val intPerson: Int = cur.getInt(index_Person)
                    val strbody: String = cur.getString(index_Body)
                    val longDate: Long = cur.getLong(index_Date)
                    val intType: Int = cur.getInt(index_Type)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    val d = Date(longDate)
                    val strDate: String = dateFormat.format(d)
                    val strType = when (intType) {
                        1 -> {
                            "接收"
                        }
                        2 -> {
                            "发送"
                        }
                        3 -> {
                            "草稿"
                        }
                        4 -> {
                            "发件箱"
                        }
                        5 -> {
                            "发送失败"
                        }
                        6 -> {
                            "待发送列表"
                        }
                        0 -> {
                            "所以短信"
                        }
                        else -> {
                            "null"
                        }
                    }

                    smsBuilder.append("[ ")
                    smsBuilder.append("$strAddress, ")
                    smsBuilder.append("$intPerson, ")
                    smsBuilder.append("$strbody, ")
                    smsBuilder.append("$strDate, ")
                    smsBuilder.append(strType)
                    smsBuilder.append(" ]\n\n")
                } while (cur.moveToNext())
                if (!cur.isClosed()) {
                    cur.close()
                    cur = null
                }
            } else {
                smsBuilder.append("no result!")
            }
            smsBuilder.append("getSmsInPhone has executed!")
        } catch (ex: SQLiteException) {
            Log.d("SQLiteException in getSmsInPhone", ex.message.toString())
        }
        return smsBuilder.toString()
    }

}