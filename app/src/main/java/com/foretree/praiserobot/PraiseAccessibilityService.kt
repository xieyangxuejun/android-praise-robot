package com.foretree.praiserobot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent


/**
 * Created by silen on 2018/9/23 21:45
 * Copyright (c) 2018 in FORETREE
 */
class PraiseAccessibilityService: AccessibilityService() {
    private val mHandler = Handler()

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            packageNames = arrayOf(PackageNames.ELEME.name)
            notificationTimeout = 100
        }
        Log.d("==>", "PraiseAccessibilityService ==> onServiceConnected")
    }

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventType = event.eventType
        val className = event.className
        Log.d("==>", className.toString())
        when(eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val window = rootInActiveWindow?:return
                if (className.startsWith("me.ele.order.ui.rate.o")
                        ||className.startsWith("me.ele.order.ui.rate.OrderRateActivity")) {
                    mHandler.postDelayed({
                        Utils.performClickByText(window,"超赞")
                        Utils.performClickByText(window,"匿名评价")
                        Utils.performClickByViewId(window,"me.ele:id/five")
                        mHandler.postDelayed({
                            Utils.performClickByViewIdMore(window, "me.ele:id/five")
                            mHandler.postDelayed({
                                Utils.performSetText(window,"me.ele:id/edit_text", "好评!!!")
                            }, 400)
                        }, 400)
                        mHandler.postDelayed({
                            Utils.performClickByText(window, "提交评价")
                            event.source.performAction(AccessibilityService.GLOBAL_ACTION_BACK)
                        }, 400)
                    }, 400)
                } else if (className.startsWith("me.ele.application.ui.home.d")
                        ||className.startsWith("me.ele.application.ui.home.HomeActivity")) {
                    mHandler.postDelayed({
                        Utils.performClickByText(window, "评价得")
                    }, 400)
                }
            }
        }
    }

}