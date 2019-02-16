package com.foretree.praiserobot

import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.apache.commons.io.IOUtils
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL

/**
 * For more information, you can visit https://github.com/xieyangxuejun or contact me by xieyangxuejun@gmail.com
 * @author silen
 * @time 2019/1/18 14:34
 * @des
 * Copyright (c) 2019 in FORETREE
 */
class RobotRequest {
    companion object {
        @JvmStatic
        val api = "http://openapi.tuling123.com/openapi/api/v2"
    }

    fun post(text: String, consumer: Consumer<String>) = Observable.create(ObservableOnSubscribe<String> {
        try {
            val url = URL(api)
            val gson = Gson()
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 60 * 1000
                readTimeout = 60 * 1000
                doOutput = true
                setChunkedStreamingMode(0)
                connect()
                val params = gson.toJson(RobotEntity(perception = Perception(InputText((text)))), RobotEntity::class.java)
                IOUtils.write(params.toByteArray(), outputStream)
//                BufferedOutputStream(outputStream).apply {
//                    val params = gson.toJson(RobotEntity(perception = Perception(InputText((text)))), RobotEntity::class.java)
//                    write(params.toByteArray())
//                    flush()
//                    close()
//                }
            }
            val response = Utils.readStream2(conn.inputStream)
            val robotResponse = gson.fromJson(response, RobotResponse::class.java)
            val text1 = robotResponse.results[0].values.text
            it.onNext(text1)
        } catch (e: Exception) {
            it.onError(e)
        }
        it.onComplete()
    }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer, Consumer {
                //ignore
                it.printStackTrace()
            })
}

data class RobotResponse(
        val intent: IntentEntity,
        val results: List<RespResult>
): Serializable

data class IntentEntity (
        val actionName: String,
        val code: Int,
        val intentName: String
): Serializable

data class RespResult(
        val groupType: Int,
        val resultType: String,
        val values: RespValues
): Serializable

data class RespValues(
        val text: String
): Serializable

data class RobotEntity(
        val reqType: Int = 0,
        val perception: Perception,
        val userInfo: UserInfo = UserInfo()
): Serializable

data class Perception(
        val inputText: InputText
): Serializable

data class InputText(
        val text: String
): Serializable

data class UserInfo(
        val apiKey: String = "6aaed5c294604a7db72cdf8a324e3b4d",
        val userId: String = "now"
): Serializable