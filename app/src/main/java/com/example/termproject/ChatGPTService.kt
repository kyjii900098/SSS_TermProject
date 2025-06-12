package com.example.termproject

import android.util.Log
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object ChatGPTService {
    private const val API_URL = "https://api.openai.com/v1/chat/completions"
    private const val API_KEY = "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"

    private val client = OkHttpClient()

    suspend fun getResponse(userMessage: String, characterType: String): String {
        return suspendCancellableCoroutine { cont ->
            try {
                val systemPrompt = when (characterType) {
                    "sanjini" -> "너는 부산대학교의 마스코트 캐릭터인 산지니처럼 행동해줘. 똑똑하고 차분한 성격을 가지고 있어. 항상 정중하게 말해줘."
                    "hobanu" -> "경북대학교의 마스코트 캐릭터인 '호반우'처럼 행동해줘. 듬직하고 신중한 성격을 가지고 있어. 항상 침착하게 응답해줘."
                    "chacha" -> "너는 충남대학교의 마스코트 캐릭터인 차차처럼 행동해줘. 활기차고 귀여운 성격이야. 항상 밝고 유쾌하게 응답해줘."
                    else -> "너는 친절한 마스코트 캐릭터야."
                }

                val messages = JSONArray()
                    .put(JSONObject()
                        .put("role", "system")
                        .put("content", systemPrompt))
                    .put(JSONObject()
                        .put("role", "user")
                        .put("content", userMessage))

                val requestBody = JSONObject()
                    .put("model", "gpt-3.5-turbo")
                    .put("messages", messages)

                val body = requestBody.toString()
                    .toRequestBody("application/json".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("ChatGPTService", "API 요청 실패: ${e.message}")
                        cont.resumeWith(Result.failure(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {  // 💡 리소스 자동 정리
                            if (it.isSuccessful) {
                                val responseBody = it.body?.string() ?: ""
                                Log.d("ChatGPTService", "응답 본문: $responseBody")

                                try {
                                    val json = JSONObject(responseBody)
                                    val message = json
                                        .getJSONArray("choices")
                                        .getJSONObject(0)
                                        .getJSONObject("message")
                                        .getString("content")

                                    cont.resumeWith(Result.success(message))
                                } catch (e: Exception) {
                                    Log.e("ChatGPTService", "응답 파싱 오류: ${e.message}")
                                    cont.resumeWith(Result.failure(e))
                                }
                            } else {
                                cont.resumeWith(Result.failure(Exception("API Error: ${it.code}")))
                            }
                        }
                    }

                })

            } catch (e: Exception) {
                cont.resumeWith(Result.failure(e))
            }
        }
    }
}
