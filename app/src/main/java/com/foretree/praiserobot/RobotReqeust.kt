package com.foretree.praiserobot

import android.util.Log
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.BufferedOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * For more information, you can visit https://github.com/xieyangxuejun or contact me by xieyangxuejun@gmail.com
 * @author silen
 * @time 2019/1/18 14:34
 * @des
 * Copyright (c) 2019 in FORETREE
 */
class RobotReqeust {
    companion object {
        @JvmStatic
        val api = "http://openapi.tuling123.com/openapi/api/v2"
    }


    fun post(text: String) {
        Observable.create(ObservableOnSubscribe<String> {
            try {
                val url = URL(api)
                val gson = Gson()
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 60 * 1000
                    readTimeout = 60 * 1000
                    doOutput = true
                    setChunkedStreamingMode(0)
                    BufferedOutputStream(outputStream).apply {
                        val params = gson.toJson(RobotEntity(perception = Perception(InputText((text)))), RobotEntity::class.java)
                        write(params.toByteArray())
                    }
                }
                val response = Utils.readStream(conn.inputStream)
                val robotResponse = gson.fromJson(response, RobotResponse::class.java)
                Log.d("====>", robotResponse.results[0].values.text)
                it.onNext(robotResponse.results[0].values.text)
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onComplete()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                }.dispose()

    }
}

data class RobotResponse(
        val intent: IntentEntity,
        val results: List<RespResult>
)

data class IntentEntity (
        val actionName: String,
        val code: Int,
        val intentName: String
)

data class RespResult(
        val groupType: Int,
        val resultType: String,
        val values: RespValues
)

data class RespValues(
        val text: String
)

data class RobotEntity(
        val reqType: Int = 0,
        val perception: Perception,
        val userInfo: UserInfo = UserInfo()
)

data class Perception(
        val inputText: InputText
)

data class InputText(
        val text: String
)

data class UserInfo(
        val apiKey: String = "6aaed5c294604a7db72cdf8a324e3b4d",
        val userId: String = "now"
)