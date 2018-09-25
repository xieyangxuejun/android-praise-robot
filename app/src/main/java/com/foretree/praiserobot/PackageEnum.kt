package com.foretree.praiserobot

/**
 * Created by silen on 2018/9/24 0:31
 * Copyright (c) 2018 in FORETREE
 */
enum class PackageEnum(val packageName: String) {
    JD("com.jingdong.app.mall"),
    ELEME("me.ele"),
    ERROR("error");

    companion object {
        fun from(packageName: String): PackageEnum {
            values().forEach {
                if (it.packageName == packageName) return it
            }
            return ERROR
        }
    }
}