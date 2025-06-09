package com.example.termproject.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object WeatherFetcher {
    suspend fun fetchWeatherGreeting(): String = withContext(Dispatchers.IO) {
        val serviceKey =
            "qvAMhThL7ZEBL+V4L8GMLNX+yH8QaeAhHh6GZKBdRqjcC8nI0xhml0pdcKR9QBdn3xfkl+x0Ow+dLCl5wcubig=="

        val now = Calendar.getInstance()
        val date = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(now.time)
        val time = getBaseTime(now)

        val url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst" +
                "?serviceKey=$serviceKey" +
                "&numOfRows=60&pageNo=1&dataType=JSON" +
                "&base_date=$date&base_time=$time&nx=60&ny=127"

        val request = Request.Builder().url(url).build()
        val response = OkHttpClient().newCall(request).execute()

        val json = response.body?.string() ?: return@withContext "날씨 정보를 불러올 수 없어요"
        val jsonArray = JSONObject(json)
            .getJSONObject("response")
            .getJSONObject("body")
            .getJSONObject("items")
            .getJSONArray("item")

        var pty = "0"
        var sky = "1"

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            when (item.getString("category")) {
                "PTY" -> pty = item.getString("fcstValue")
                "SKY" -> sky = item.getString("fcstValue")
            }
        }

        return@withContext when {
            pty != "0" -> "오늘은 비가 와요 ☔ 우산 잊지 마세요!"
            sky == "1" -> "맑고 화창한 하루예요! ☀️"
            sky == "3" -> "구름이 조금 있지만 괜찮아요 ☁️"
            sky == "4" -> "흐린 날씨네요 🌥 오늘도 힘내요!"
            else -> "오늘도 좋은 하루 되세요!"
        }
    }

    private fun getBaseTime(now: Calendar): String {
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val baseHours = listOf(2, 5, 8, 11, 14, 17, 20, 23)
        val closestHour = baseHours.lastOrNull { it <= hour } ?: 23
        return String.format("%02d00", closestHour)
    }
}