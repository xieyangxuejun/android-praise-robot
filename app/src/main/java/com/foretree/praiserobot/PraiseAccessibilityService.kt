package com.foretree.praiserobot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent


/**
 * Created by silen on 2018/9/23 21:45
 * Copyright (c) 2018 in FORETREE
 */
class PraiseAccessibilityService: AccessibilityService() {

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            packageNames = arrayOf(PackageEnum.ELEME.name)
            notificationTimeout = 100
        }
        Log.d("==>", "PraiseAccessibilityService ==> onServiceConnected")
    }

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when(PackageEnum.valueOf(event.packageName.toString())) {
            PackageEnum.ELEME -> {
                PraiseHelper.handleEleMe(event, rootInActiveWindow?:return)
            }
        }
    }

}