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
object PraiseAccessibilityAction {
    @JvmStatic
    private val mHandler = Handler()
    @JvmStatic
    private val DEFAULT_MILLIS: Long  = 400

    @JvmStatic
    fun handleJD(event: AccessibilityEvent, window: AccessibilityNodeInfo) {
        val eventType = event.eventType
        val className = event.className
        Log.d("==>", className.toString())
        when(eventType) {
            //点击待评价, 进入评价中心
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                mHandler.postDelayed({
                    Utils.performClickByText(window,"评价晒单")
                    mHandler.postDelayed({
                        //Utils.performClickByViewId(window,"com.jd.lib.evaluatecenter:id/coo_edit_content")
                        Utils.performSetText(window,
                                "com.jd.lib.evaluatecenter:id/coo_edit_content",
                                "买了很多东西 都非常满意 很好的卖家 我会常来的 折扣卡可以升到顶级了吧！")
                        Utils.performClickByViewIdMore(window, "com.jd.lib.evaluatecenter:id/lottie_view")
                        mHandler.postDelayed({
                            Utils.performClickByText(window, "提交")
                        }, DEFAULT_MILLIS)
                    }, DEFAULT_MILLIS)
                }, DEFAULT_MILLIS)
            }
        }
    }

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
                                Utils.performSetText( window,"me.ele:id/edit_text", "好评!!!")
                            }, 400)
                        }, 400)
                        mHandler.postDelayed({
                            Utils.performClickByText(window, "提交评价")
                            event.source.performAction(AccessibilityService.GLOBAL_ACTION_BACK)
                        }, 400)
                    }, 400)
                } else if (className.startsWith("me.ele.application.ui.home.d")
                        ||className.startsWith("me.ele.application.ui.home.HomeActivity")
                        ||className.startsWith("me.ele.ny")) {
                    mHandler.postDelayed({
                        Utils.performClickByText(window, "评价得")
                    }, 400)
                }
            }
        }
    }

    private var nowLivePreNickname: CharSequence = ""
    fun handleNOW(event: AccessibilityEvent, window: AccessibilityNodeInfo) {
        when(event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Utils.getAccessibilityNodeInfosByViewId(window, "com.tencent.now:id/bij").run {
                    if (this != null && this.isNotEmpty()) {
                        val contentText = this[size - 1].text
                        val nickname = contentText.toString().replace("来了", "").trim()
                        if (contentText != null && contentText.endsWith("来了") && nowLivePreNickname != nickname) {
                            Log.d("==>", "text=$contentText" + ", address=${this}")
                            Utils.performSetText(window, "com.tencent.now:id/ajc", "欢迎 $nickname")
                            mHandler.postDelayed({
                                Utils.performClickByViewId(window, "com.tencent.now:id/asv")
                                nowLivePreNickname = nickname
                            }, 100)
                        }
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {

            }
        }
    }
}
