package com.foretree.praiserobot

import android.accessibilityservice.AccessibilityService
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import io.reactivex.functions.Consumer
import java.util.regex.Pattern

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
                            }, DEFAULT_MILLIS)
                        }, DEFAULT_MILLIS)
                        mHandler.postDelayed({
                            Utils.performClickByText(window, "提交评价")
                            event.source.performAction(AccessibilityService.GLOBAL_ACTION_BACK)
                        }, DEFAULT_MILLIS)
                    }, DEFAULT_MILLIS)
                } else if (className.startsWith("me.ele.application.ui.home.d")
                        || className.startsWith("me.ele.application.ui.home.HomeActivity")
                        || className.startsWith("me.ele.ny")) {
                    mHandler.postDelayed({
                        Utils.performClickByText(window, "评价得")
                    }, DEFAULT_MILLIS)
                }
            }
        }
    }

    //获取now礼物文字
    private fun getParentFromText(window: AccessibilityNodeInfo) {

    }

    private var mWelcomeNickname: CharSequence = ""
    private var mWelcomeContent: CharSequence = ""
    private var mWelcomeVipContent: CharSequence = ""
    private var mAttentionContent: CharSequence = ""
    private var mShowTimeContent: Boolean = true
    private var mGiftArray: ArrayList<String> = arrayListOf()
    private var mIsGetFlower = false
    private var mPreNickname: CharSequence = ""
    //最新的文字防止刷屏
    private var mLastContent: CharSequence = ""
    private var mLastMVPContent: CharSequence = ""
    //机器人请求
    private var mRobotRequest = RobotRequest()
    private var mPreQuestion = ""

    fun handleNOW2(event: AccessibilityEvent, window: AccessibilityNodeInfo, context: Context) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {

                //------------欢迎
                SharePreferenceManager.getInstance(context).run {
                    try {
                        //定时
                        actionScheduleTips(context, window)
                        Utils.getAccssibilityNodeInfosByText(window, " ").apply {
                            if (this != null && this.isNotEmpty()) {
                                val content = get(size - 1).text
                                if (mLastContent.toString().equals(content.toString())) return
                                else mLastContent = content
                            }
                        }
                        //机器人
                        if (getChkRobot()) {
                            Utils.getAccssibilityNodeInfosByText(window, "QQQ").run {
                                if (this != null && this.isNotEmpty()) {
                                    val contentText = this[size - 1].text
                                    if (contentText != null && contentText.contains(": QQQ")) {
                                        val ques = contentText.split("QQQ")[1]
                                        if (ques.isNotEmpty() && ques != mPreQuestion) {
                                            mPreQuestion = ques
                                            mRobotRequest.post(ques, Consumer {
                                                sendMsg(window, it)
                                            })
                                        }
                                    }
                                }
                            }
                        }

                        //设置昵称
                        if (getNicknameSettings()) {
                            Utils.getAccssibilityNodeInfosByText(window, "设置亲密昵称=").run {
                                if (this != null && this.isNotEmpty()) {
                                    val contentText = this[size - 1].text
                                    if (contentText != null && contentText.contains(":")) {
                                        val list = contentText.split("设置亲密昵称=")
                                        val key = list[0].replace(":", "").trim()
                                        val nick = list[1]
                                        if (key.isNotEmpty()
                                                && nick.isNotEmpty() && mPreNickname != nick) {
                                            putSavedNickname(key, nick)
                                            sendMsg(window, "${nick}设置成功!!!")
                                            mPreNickname = nick
                                        }
                                    }
                                }
                            }
                        }

                        if (getShowMvpSettings()) {
                            Utils.getAccssibilityNodeInfosByText(window, "获得MVP称号").run {
                                if (this != null && this.isNotEmpty()) {
                                    val contentText = this[size - 1].text
                                    if (contentText != null
                                            && contentText.contains(":")
                                            && contentText.toString() != mLastMVPContent) {
                                        val regex = "(?<=\\[)(\\S+)(?=\\])|(?<=【)[^】]*"
                                        val pattern = Pattern.compile(regex)
                                        val matcher = pattern.matcher(contentText)
                                        while (matcher.find()) {
                                            val group = matcher.group(0)
                                            if (group.isNotEmpty()) {
                                                val randomText = Utils.randomText(group)
                                                sendMsg(window, randomText)
                                                mLastMVPContent = contentText
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 自动领花花
                        if (getFlowerSetting()) {
                            actionGetFlower(window, context)
                        }

                        if (getGift()) {
                            //礼物
                            actionShowGift(window, context)
                        }

                        if (getWelcome()) {
                            if (getWelcomeVIP()) {
                                //贵族
                                welcomeVIP(window, context)
                            } else {
                                welcomeVIP(window, context)
                                welcomeNormal(window, context)
                            }
                        }
                        // -----------关注
                        if (getFollow()) {
                            actionFollow(window, context)
                        }
                    } catch (e: Exception) {
                        //igore
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {

            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (mIsGetFlower) {

                    mHandler.postDelayed({
                        try {
                            val child = window.getChild(3)
                            if (child != null && "android.widget.ImageView" == child.className) {
                                child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, 500)
                    mIsGetFlower = false
                }
            }
        }
    }

    private fun SharePreferenceManager.actionScheduleTips(context: Context, window: AccessibilityNodeInfo) {
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
                //                            var name = ""
                //                            try {
                //                                name = window.findAccessibilityNodeInfosByText("人")
                //                                        .let {
                //                                            it[0].parent.getChild(1).text.toString()
                //                                        }
                //                            } catch (e: Exception) {
                //                            }
                mHandler.postDelayed({
                    //                                sendMsg(window, if (name.isEmpty()) this else this.format(name))
                    sendMsg(window, this)
                    mShowTimeContent = true
                }, timeTips)
                mShowTimeContent = false
            }
        }
    }

    private fun actionGetFlower(window: AccessibilityNodeInfo, context: Context) {
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
            if (start != 0 && end - start > 2 ) {
                window.getChild(start + 1).apply {
                    if ("新人主播" != text.toString()) {
                        performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        mIsGetFlower = true
                    }
                }
            }
        }
    }

    private fun getNickname(context: Context, key: String?): String {
        if (key == null) return ""
        return SharePreferenceManager.getInstance(context).getSavedNickname(key) ?: key
    }

    private fun actionShowGift(window: AccessibilityNodeInfo, context: Context) {
        Utils.getAccssibilityNodeInfosByText(window, "送一个")?.run {
            try {
                forEach { info ->
                    val key = info.parent.run {
                        "${getNickname(context, getChild(1).text?.toString())}${getChild(2).text
                                ?: ""}"
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
                            sendMsg(window, "谢谢 $key!")
                            mGiftArray.remove(key)
                        }
                    } else {

                    }
                } else if (mGiftArray.size > 2) {
                    mGiftArray.forEachIndexed { index, _ ->
                        if (index >= 2) {
                            val key = mGiftArray[index]
                            sendMsg(window, "谢谢 $key!")
                            mGiftArray.remove(key)
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun actionFollow(window: AccessibilityNodeInfo, context: Context) {
        Utils.getAccssibilityNodeInfosByText(window, "关注了主播").run {
            if (this != null && this.isNotEmpty()) {
                val contentText = this[size - 1].text
                if (contentText != null
                        && !contentText.contains(":")
                        && !mAttentionContent.contains(contentText)) {

                    val nickname = contentText.toString().replace("关注了主播", "").trim()
                    val comment = "谢谢 ${getNickname(context, nickname)} 的关注哟\uD83D\uDE17"
                    sendMsg(window, comment)
                    mAttentionContent = contentText
                }
            }
            //
        }
    }

    private fun welcomeNormal(window: AccessibilityNodeInfo, context: Context) {
        Utils.getAccssibilityNodeInfosByText(window, "来了").run {
            if (this != null && this.isNotEmpty()) {
                val contentText = this[size - 1].text
                if (contentText != null
                        && !contentText.contains(":")
                        && !mWelcomeContent.contains(contentText)) {
                    sendWelcomeMsg(context, contentText.toString(), window)
                    mWelcomeContent = contentText
                }
            }
        }
    }

    private fun sendWelcomeMsg(context: Context, contentText: String, window: AccessibilityNodeInfo) {
        val nickname = clearNickname(contentText)
        if (nickname != mWelcomeNickname) {
            val comment = "欢迎 ${getNickname(context, nickname)} 大驾光临\uD83D\uDE17"
            sendMsg(window, comment)
            mWelcomeNickname = nickname
        }
    }

    private fun clearNickname(contentText: String): String = contentText
            .replace("icon  ", "")
            .replace("来了", "")
            .trim()

    private fun welcomeVIP(window: AccessibilityNodeInfo, context: Context) {
        Utils.getAccssibilityNodeInfosByText(window, "icon  ").run {
            if (this != null && this.isNotEmpty()) {
                val contentText = this[size - 1].text
                if (contentText != null
                        && !contentText.contains(":")
                        && !mWelcomeVipContent.contains(contentText)) {
                    sendWelcomeMsg(context, contentText.toString(), window)
                    mWelcomeVipContent = contentText
                }
            }
        }
    }

    private fun sendMsg(window: AccessibilityNodeInfo, comment: String) {
        //not empty
        if (comment.isNotEmpty()) {
            mHandler.postDelayed({
                Utils.performSetTextFromFocus(window, comment)
                Utils.performClickByText(window, "发送")
            }, 0)
        }
    }
}
