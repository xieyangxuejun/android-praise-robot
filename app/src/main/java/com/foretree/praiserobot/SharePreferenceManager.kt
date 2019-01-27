package com.foretree.praiserobot

import android.annotation.SuppressLint
import android.content.Context

/**
 * Created by silen on 2018/10/25 4:29
 * Copyright (c) 2018 in FORETREE
 */
class SharePreferenceManager(private val context: Context) {

    companion object {
        @JvmStatic
        val KEY_TIPS_TIME_POSITION = "tips_time_position"
        @JvmStatic
        val KEY_WELCOME = "welcome"
        @JvmStatic
        val KEY_VIP_WELCOME = "vip_welcome"
        @JvmStatic
        val KEY_GIFT = "gift"
        @JvmStatic
        val KEY_FOLLOW = "follow"
        @JvmStatic
        val KEY_FANS = "fans"
        @JvmStatic
        val KEY_FLOWER = "flower"
        @JvmStatic
        val KEY_ATTENTION_CONTENT = "attention_content"
        @JvmStatic
        val NAME_LIVE_SETTINGS = "now_live_settings"

        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        private var mInstance: SharePreferenceManager? = null

        @JvmStatic
        fun getInstance(context: Context): SharePreferenceManager {
            return mInstance ?: synchronized(this) {
                SharePreferenceManager(context).apply {
                    mInstance = this
                }
            }
        }
    }

    private val mSp: SharePreferenceUtil = SharePreferenceUtil.getPrefs(context, NAME_LIVE_SETTINGS)


    fun getWelcome() = mSp.getBoolean(KEY_WELCOME, true)
    fun putWelcome(boolean: Boolean) = mSp.putBoolean(KEY_WELCOME, boolean).save()
    fun putWelcomeVIP(boolean: Boolean) = mSp.putBoolean(KEY_VIP_WELCOME, boolean).save()
    fun getWelcomeVIP() = mSp.getBoolean(KEY_VIP_WELCOME, false)

    fun getGift() = mSp.getBoolean(KEY_GIFT, false)
    fun putGift(boolean: Boolean) = mSp.putBoolean(KEY_GIFT, boolean).save()
    fun getFollow() = mSp.getBoolean(KEY_FOLLOW, false)
    fun putFollow(boolean: Boolean) = mSp.putBoolean(KEY_FOLLOW, boolean).save()
    fun getFans() = mSp.getBoolean(KEY_FANS, false)
    fun putFans(boolean: Boolean) = mSp.putBoolean(KEY_FANS, boolean).save()

    fun getFlowerSetting() = mSp.getBoolean(KEY_FLOWER, false)
    fun putFlowerSetting(boolean: Boolean) = mSp.putBoolean(KEY_FLOWER, boolean).save()

    fun getAttentionContent(): String = mSp.getString(KEY_ATTENTION_CONTENT, "")
    fun putAttentionContent(string: String) = mSp.putString(KEY_ATTENTION_CONTENT, string).save()

    fun getTipsTimePosition() = mSp.getInt(KEY_TIPS_TIME_POSITION, 0)
    fun putTipsTimePosition(position: Int) = mSp.putInt(KEY_TIPS_TIME_POSITION, position).save()
}