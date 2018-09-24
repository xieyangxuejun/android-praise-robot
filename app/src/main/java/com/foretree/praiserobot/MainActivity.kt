package com.foretree.praiserobot

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mIRemoteService: IPraiseRemoteService? = null

    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mIRemoteService = null
        }

        @SuppressLint("SetTextI18n")
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mIRemoteService = IPraiseRemoteService.Stub.asInterface(service)
            tv_pid.text = "service pid:${mIRemoteService?.pid.toString()}"
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, PraiseNoticeService::class.java).apply {
            action = IPraiseRemoteService::class.java.name
        }
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mConnection)
    }
}
