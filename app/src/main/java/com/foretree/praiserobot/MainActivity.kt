package com.foretree.praiserobot

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.AdapterView
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
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initService()
    }

    private fun initView() {
        SharePreferenceManager.getInstance(this).run {
            switch1.run {
                isChecked = getWelcome()
                setOnCheckedChangeListener { _, isChecked ->
                    putWelcome(isChecked)
                }
            }
            switch2.run {
                isChecked = getGift()
                setOnCheckedChangeListener { _, isChecked ->
                    putGift(isChecked)
                }
            }
            switch3.run {
                isChecked = getFollow()
                setOnCheckedChangeListener { _, isChecked ->
                    putFollow(isChecked)
                }
            }
            switch4.run {
                isChecked = getFans()
                setOnCheckedChangeListener { _, isChecked ->
                    putFans(isChecked)
                }
            }
            //抢花花
            switch_flower.run {
                isChecked = getFlowerSetting()
                setOnCheckedChangeListener { _, isChecked ->
                    putFlowerSetting(isChecked)
                }
            }
            et_attention.setText(getAttentionContent())
            btn_save.setOnClickListener {
                putAttentionContent(et_attention.text.toString())
            }

            //定时
            spinner_time.setSelection(getTipsTimePosition())
            spinner_time.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    putTipsTimePosition(position)
                }
            }
            //
            spinner.setSelection(if (getWelcomeVIP()) 1 else 0)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    putWelcomeVIP(position != 0)
                }
            }
        }
    }

    fun initService() {
        val intent = Intent(this, PraiseNoticeService::class.java).apply {
            action = IPraiseRemoteService::class.java.name
        }
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mConnection)
    }

    fun clickOpen(view: View) {
        Utils.startAccessibilitySettings(this)
    }
}
