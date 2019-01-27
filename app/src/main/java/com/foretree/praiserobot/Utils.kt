package com.foretree.praiserobot

import android.annotation.TargetApi
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.FOCUS_INPUT
import java.io.InputStream

/**
 * Created by silen on 2018/9/23 23:38
 * Copyright (c) 2018 in FORETREE
 */
object Utils {

    @JvmStatic
    fun readStream(input: InputStream)
            : String {
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return String(bytes)
    }

    @JvmStatic
    fun getAccssibilityNodeInfosByText(window: AccessibilityNodeInfo, content: String): MutableList<AccessibilityNodeInfo>? {
        return window.findAccessibilityNodeInfosByText(content)
    }

    @JvmStatic
    fun getAccessibilityNodeInfosByFocus(window: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        return window.findFocus(FOCUS_INPUT)
    }

    //get info list
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @JvmStatic
    fun getAccessibilityNodeInfosByViewId(window: AccessibilityNodeInfo, viewId: String): MutableList<AccessibilityNodeInfo>? {
        return window.findAccessibilityNodeInfosByViewId(viewId)
    }

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun performSetText(window: AccessibilityNodeInfo, viewId: String, comment: String): Boolean {
        return window.findAccessibilityNodeInfosByViewId(viewId).run {
            if (this != null && !isEmpty()) {
                val info = get(0)
                info.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    val bundle = Bundle().apply {
                        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, comment)
                    }
                    info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
                } else {
                    info.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                    info.performAction(AccessibilityNodeInfo.ACTION_PASTE)
                }
            } else {
                false
            }
        }
    }

    @JvmStatic
    fun performSetTextFromFocus(window: AccessibilityNodeInfo, comment: String): Boolean {
        return getAccessibilityNodeInfosByFocus(window).run {
            if (this != null) {
                this.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    val bundle = Bundle().apply {
                        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, comment)
                    }
                    this.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
                } else {
                    this.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                    this.performAction(AccessibilityNodeInfo.ACTION_PASTE)
                }
            } else {
                false
            }
        }
    }

    @JvmStatic
    fun performClip(context: Context, text: String) {
        val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText("text", text)
    }


    @JvmStatic
    fun performClickByText(window: AccessibilityNodeInfo, text: String): Boolean {
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
    fun performClickByViewId(window: AccessibilityNodeInfo, viewId: String): Boolean {
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
    fun performClickByViewIdMore(window: AccessibilityNodeInfo, viewId: String): Boolean {
        return window.findAccessibilityNodeInfosByViewId(viewId).run {
            if (this != null && !isEmpty()) {
                for (i in 0..(size - 1)) {
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
        } catch (e: Settings.SettingNotFoundException) {
        }

        if (ok == 1) {
            val settingValue = Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            TextUtils.SimpleStringSplitter(':')
                    .run {
                        setString(settingValue ?: return false)
                        while (this.hasNext()) {
                            val next = next().toLowerCase()
                            if (next == service.toLowerCase()) return true
                        }
                    }
        }
        return false
    }

    /* 获取手机屏幕的宽度 */
    fun getMetricsWidth(context: Context?): Int {
        return context?.resources?.displayMetrics?.widthPixels ?: 0
    }

    /* 获取手机屏幕的高度 */
    fun getMetricsHeight(context: Context?): Int {
        return context?.resources?.displayMetrics?.heightPixels ?: 0
    }

    @JvmStatic
    fun startAccessibilitySettings(context: Context) = context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}