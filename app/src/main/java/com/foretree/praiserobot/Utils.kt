package com.foretree.praiserobot

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Created by silen on 2018/9/23 23:38
 * Copyright (c) 2018 in FORETREE
 */
object Utils {

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun performSetText(window: AccessibilityNodeInfo, viewId: String, comment: String):Boolean {
        return window.findAccessibilityNodeInfosByText(viewId).run {
            if (this != null && !isEmpty()) {
                get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
                    putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, comment)
                })
            } else {
                false
            }
        }
    }
    @JvmStatic
    fun performClickByText(window: AccessibilityNodeInfo, text: String):Boolean {
        return window.findAccessibilityNodeInfosByText(text).run {
            if (this != null && !isEmpty()) {
                get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK)
            } else {
                false
            }
        }
    }

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun performClickByViewId(window: AccessibilityNodeInfo, viewId: String):Boolean {
        return window.findAccessibilityNodeInfosByViewId(viewId).run {
            if (this != null && !isEmpty()) {
                get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK)
            } else {
                false
            }
        }
    }

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun performClickByViewIdMore(window: AccessibilityNodeInfo, viewId: String):Boolean {
        return window.findAccessibilityNodeInfosByViewId(viewId).run {
            if (this != null && !isEmpty()) {
                for (i in 0..(size-1)) {
                    get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
                true
            } else {
                false
            }
        }
    }

    /**
     * settings是否开启
     */
    @JvmStatic
    fun checkStateFeature(context: Context, service: String): Boolean {
        var ok = 0
        try {
            ok = Settings.Secure.getInt(context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        } catch (e: Settings.SettingNotFoundException) {}

        if (ok == 1) {
            val settingValue = Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                    TextUtils.SimpleStringSplitter(':')
                            .run {
                                setString(settingValue?:return false)
                                while (this.hasNext()) {
                                    val next = next().toLowerCase()
                                    if (next == service.toLowerCase()) return true
                                }
                            }
        }
        return false
    }

    @JvmStatic
    fun startAccessibilitySettings(context: Context) = context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}