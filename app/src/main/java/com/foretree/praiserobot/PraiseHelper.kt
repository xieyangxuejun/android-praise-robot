package com.foretree.praiserobot

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * ele, 每日优先
 * Created by silen on 2018/9/24 0:29
 * Copyright (c) 2018 in FORETREE
 */
object PraiseHelper {
    @JvmStatic
    private val mHandler = Handler()

    @JvmStatic
    fun handleEleMe(event: AccessibilityEvent, window: AccessibilityNodeInfo ) {
        val eventType = event.eventType
        val className = event.className
        Log.d("==>", className.toString())
        when(eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
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
