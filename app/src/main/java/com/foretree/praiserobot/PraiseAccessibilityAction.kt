package com.foretree.praiserobot

import android.accessibilityservice.AccessibilityService
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * ele, 每日优先
 * Created by silen on 2018/9/24 0:29
 * Copyright (c) 2018 in FORETREE
 */
@TargetApi(Build.VERSION_CODES.DONUT)
object PraiseAccessibilityAction {
    @JvmStatic
    private val mHandler = Handler()
    @JvmStatic
    private val DEFAULT_MILLIS: Long = 400

    @JvmStatic
    fun handleJD(event: AccessibilityEvent, window: AccessibilityNodeInfo) {
        val eventType = event.eventType
        val className = event.className
        Log.d("==>", className.toString())
        when (eventType) {
            //点击待评价, 进入评价中心
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                mHandler.postDelayed({
                    Utils.performClickByText(window, "评价晒单")
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
    fun handleEleMe(event: AccessibilityEvent, window: AccessibilityNodeInfo) {
        val eventType = event.eventType
        val className = event.className
        Log.d("==>", className.toString())
        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (className.startsWith("me.ele.order.ui.rate.o")
                        || className.startsWith("me.ele.order.ui.rate.OrderRateActivity")) {
                    mHandler.postDelayed({
                        Utils.performClickByText(window, "超赞")
                        Utils.performClickByText(window, "匿名评价")
                        Utils.performClickByViewId(window, "me.ele:id/five")
                        mHandler.postDelayed({
                            Utils.performClickByViewIdMore(window, "me.ele:id/five")
                            mHandler.postDelayed({
                                Utils.performSetText(window, "me.ele:id/edit_text", "好评!!!")
                            }, 400)
                        }, 400)
                        mHandler.postDelayed({
                            Utils.performClickByText(window, "提交评价")
                            event.source.performAction(AccessibilityService.GLOBAL_ACTION_BACK)
                        }, 400)
                    }, 400)
                } else if (className.startsWith("me.ele.application.ui.home.d")
                        || className.startsWith("me.ele.application.ui.home.HomeActivity")
                        || className.startsWith("me.ele.ny")) {
                    mHandler.postDelayed({
                        Utils.performClickByText(window, "评价得")
                    }, 400)
                }
            }
        }
    }

    //获取now礼物文字
    private fun getParentFromText(window: AccessibilityNodeInfo) {

    }

    private var mBijPreContent: CharSequence = ""
    private var mBijVipPreContent: CharSequence = ""
    private var mAttentionContent: CharSequence = ""
    private var mShowTimeContent: Boolean = true
    private var mGiftArray: ArrayList<String> = arrayListOf()
    private var mIsGetFlower = false
    fun handleNOW2(event: AccessibilityEvent, window: AccessibilityNodeInfo, context: Context) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {

                //------------欢迎
                SharePreferenceManager.getInstance(context).run {
                    // 自动领花花
                    if (getFlowerSetting()) {
                        val childCount = window.childCount
                        if (9 < childCount && !mIsGetFlower) {
                            var start = 0
                            var end = 0
                            for (index in 0 until childCount) {
                                val child = window.getChild(index)
                                if ("android.widget.ViewFlipper" == child.className) {
                                    start = index
                                } else if ("com.tencent.tbs.core.webkit.tencent.TencentWebViewProxy\$InnerWebView" == child.className) {
                                    end = index
                                    break
                                }
                            }
                            if (start != 0 && end - start > 2) {
                                window.getChild(start + 1).performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                mIsGetFlower = true
                            }
                        }
                    }

                    if (getGift()) {
                        //礼物
                        Utils.getAccssibilityNodeInfosByText(window, "送一个")?.run {
                            try {
                                forEach { info ->
                                    val key = info.parent.run {
                                        "${getChild(1).text ?: ""}${getChild(2).text ?: ""}"
                                                .replace("一个", "的")
                                    }
                                    if (key.isNotEmpty() && !mGiftArray.contains(key)) {
                                        mGiftArray.add(0, key)
                                    }
                                }
                                //消失
                                if (size == 0) {
                                    if (mGiftArray.size == 2 || mGiftArray.size == 1) {
                                        for (key in mGiftArray) {
                                            sendMsg(window, "感谢 $key")
                                            mGiftArray.remove(key)
                                        }
                                    } else {

                                    }
                                } else if (mGiftArray.size > 2) {
                                    mGiftArray.forEachIndexed { index, content ->
                                        if (index >= 2) {
                                            val key = mGiftArray[index]
                                            sendMsg(window, "感谢 $key")
                                            mGiftArray.remove(key)
                                        }
                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    if (getWelcome()) {
                        if (getWelcomeVIP()) {
                            //贵族
                            Utils.getAccssibilityNodeInfosByText(window, "icon").run {
                                if (this != null && this.isNotEmpty()) {
                                    val contentText = this[size - 1].text
                                    if (contentText != null
                                            && !contentText.contains(":")
                                            && !mBijVipPreContent.contains(contentText)) {

                                        val comment = "欢迎 ${contentText.toString().replace("icon  ", "").trim()}"
                                        sendMsg(window, comment)
                                        mBijVipPreContent = contentText
                                    }
                                }
                            }
                        } else {
                            Utils.getAccssibilityNodeInfosByText(window, "来了").run {
                                if (this != null && this.isNotEmpty()) {
                                    val contentText = this[size - 1].text
                                    if (contentText != null
                                            && !contentText.contains(":")
                                            && !mBijPreContent.contains(contentText)) {

                                        val comment = "欢迎 ${contentText.toString().replace("来了", "").trim()}"
                                        sendMsg(window, comment)
                                        mBijPreContent = contentText
                                    }
                                }
                            }
                        }
                    }
                    // -----------关注
                    if (getFollow()) {
                        Utils.getAccssibilityNodeInfosByText(window, "关注了主播").run {
                            if (this != null && this.isNotEmpty()) {
                                val contentText = this[size - 1].text
                                if (contentText != null
                                        && !contentText.contains(":")
                                        && !mAttentionContent.contains(contentText)) {

                                    val comment = "谢谢 ${contentText.toString().replace("关注了主播", "").trim()} 的关注哟"
                                    sendMsg(window, comment)
                                    Log.d("==>2", comment)
                                    mAttentionContent = contentText
                                }
                            }
                            //
                        }
                    }
                    //定时
                    var timeTips = 0L
                    try {
                        timeTips = context.resources
                                .getStringArray(R.array.array_time)[getTipsTimePosition()]
                                .run {
                                    replace("s", "").toLong() * 1000
                                }
                    } catch (e: Exception) {
                    }
                    getAttentionContent().run {
                        if (isNotEmpty() && mShowTimeContent && timeTips != 0L) {
                            val name = window.findAccessibilityNodeInfosByText("人")
                                    .let {
                                        try {
                                            it[0].parent.getChild(1).text.toString()
                                        } catch (e: Exception) {
                                            ""
                                        }
                                    }
                            mHandler.postDelayed({
                                sendMsg(window, this.format(name))
                                mShowTimeContent = true
                            }, timeTips)
                            mShowTimeContent = false
                        }
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {

            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (mIsGetFlower) {
                    mHandler.postDelayed({
                        Log.d("===>", "领花花")
                        val child = window.getChild(3)
                        if (child != null && "android.widget.ImageView" == child.className) {
                            child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        }
                    }, 500)
                    mIsGetFlower = false
                }
            }
        }
    }

    @JvmStatic
    fun sendMsg(window: AccessibilityNodeInfo, comment: String) {
        //not empty
        if (comment.isNotEmpty()) {
            Utils.performSetTextFromFocus(window, comment)
            mHandler.postDelayed({
                Utils.performClickByText(window, "发送")
            }, 100)
        }
    }

    private
    var currentGiftNickname: CharSequence = ""

    @Deprecated("动态id,不能用")
    fun handleNOW(event: AccessibilityEvent, window: AccessibilityNodeInfo) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                //动态id "com.tencent.now:id/bij"
                Utils.getAccessibilityNodeInfosByViewId(window, "com.tencent.now:id/bij").run {
                    if (this != null && this.isNotEmpty()) {
                        val contentText = this[size - 1].text

                        if (contentText != null
                                && !contentText.contains(":")
                                && mBijPreContent != contentText) {
                            //欢迎
                            val comment = contentText.toString().let {
                                //欢迎
                                if (it.contains("来了")) {
                                    "欢迎 ${it.replace("来了", "").trim()}"
                                } else if (it.contains("关注了主播")) {  //关注
                                    "谢谢 ${it.replace("关注了主播", "").trim()} 的关注"
                                }
                                ""
                            }

                            //not empty
                            if (comment.isNotEmpty()) {
                                Utils.performSetText(window,
                                        "com.tencent.now:id/ajc",
                                        comment)
                                mHandler.postDelayed({
                                    Utils.performClickByViewId(window, "com.tencent.now:id/asv")
                                }, 100)
                                mBijPreContent = contentText
                            }
                        }
                    }
                }
                //礼物
                Utils.getAccessibilityNodeInfosByViewId(window, "com.tencent.now:id/a7s").run {
                    if (this != null && this.isNotEmpty()) {
                        val contentText = this[size - 1].text
                        try {
                            val list = contentText.split("送了")
                            val nickname = list[0].replace(" ", "")
                            if (contentText != null && !contentText.contains(":") && nickname != currentGiftNickname) {
                                Log.d("==>", "nickname=$nickname")
                                mHandler.postDelayed({
                                    Utils.performSetText(window, "com.tencent.now:id/ajc", "感谢 $nickname 的礼物")
                                    mHandler.postDelayed({
                                        Utils.performClickByViewId(window, "com.tencent.now:id/asv")
                                    }, 100)
                                }, 1500)
                                currentGiftNickname = nickname
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                //关注
                ///Utils.getAccessibilityNodeInfosByViewId()
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {

            }
        }
    }
}
