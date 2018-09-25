package com.foretree.praiserobot

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Created by silen on 2018/9/26 0:22
 * Copyright (c) 2018 in FORETREE
 */
interface Praiser {
    fun handle(event: AccessibilityEvent, window: AccessibilityNodeInfo)
}