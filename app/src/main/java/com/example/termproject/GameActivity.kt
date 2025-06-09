package com.example.termproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.termproject.util.WeatherFetcher
import com.example.termproject.util.WeatherUtil
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class GameActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        private const val LOCATION_PERMISSION_CODE = 1001
    }

    private lateinit var healthBar: ProgressBar
    private lateinit var moodBar: ProgressBar
    private lateinit var petNameText: TextView
    private lateinit var petImageView: ImageView
    private lateinit var speechBubble: TextView

    private lateinit var animationHandler: Handler
    private lateinit var animationRunnable: Runnable
    private var isFirstFrame = true

    private var health = 80
    private var mood = 70
    private var frame1Res = R.drawable.bird_frame1
    private var frame2Res = R.drawable.bird_frame2

    private var latestGrid: WeatherUtil.GridPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        healthBar = findViewById(R.id.healthBar)
        moodBar = findViewById(R.id.moodBar)
        petNameText = findViewById(R.id.petNameText)
        petImageView = findViewById(R.id.petImageView)
        speechBubble = findViewById(R.id.speechBubble)

        val petName = intent.getStringExtra("petName") ?: "이름 없음"
        val petImageResId = intent.getIntExtra("petImageResId", 0)
        val characterType = intent.getStringExtra("characterType") ?: "sanjini"
        health = intent.getIntExtra("health", 80)
        mood = intent.getIntExtra("mood", 70)

        petNameText.text = "이름: $petName"

        // 캐릭터에 따라 애니메이션 프레임 설정
        when (characterType) {
            "sanjini" -> {
                frame1Res = R.drawable.bird_frame1
                frame2Res = R.drawable.bird_frame2
            }
            "hobanu" -> {
                frame1Res = R.drawable.cow_frame1
                frame2Res = R.drawable.cow_frame2
            }
            "chacha" -> {
                frame1Res = R.drawable.horse_frame1
                frame2Res = R.drawable.horse_frame2
            }
        }

        Log.d("GameDebug", "onCreate 시작")
        Log.d("GameDebug", "characterType: $characterType, petImageResId: $petImageResId")

        // 초기 이미지 설정 및 애니메이션 시작
        petImageView.setImageResource(frame1Res)
        startImageAnimation()

        updateStatusBars()

        // 위치 권한 요청 or 날씨 로딩
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE)
        } else {
            requestWeatherWithLocation()
        }

        // 버튼 리스너 설정
        setupButtons(petImageResId, petName, characterType)
        startAutoDecrease()

        // 날씨 인삿말 표시 (선택적으로)
        launch {
            try {
                val greeting = WeatherFetcher.fetchWeatherGreeting()
                speechBubble.text = greeting
                speechBubble.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("WeatherGreeting", "날씨 인삿말 오류: ${e.message}")
            }
        }
    }

    private fun setupButtons(petImageResId: Int, petName: String, characterType: String) {
        findViewById<Button>(R.id.talkButton).setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            mood = (mood + 10).coerceAtMost(100)
            intent.putExtra("petName", petName) // 필요시 이름 전달
            intent.putExtra("petImageResId", petImageResId) // 필요시 캐릭터도
            intent.putExtra("characterType", characterType)
            startActivity(intent)
            updateStatusBars()
        }


        findViewById<Button>(R.id.feedButton).setOnClickListener {
            val intent = Intent(this, FeedActivity::class.java).apply {
                putExtra("petImageResId", petImageResId)
                putExtra("characterType", characterType)
                putExtra("petName", petName)
                putExtra("health", health)
                putExtra("mood", mood)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.sleepButton).setOnClickListener {
            val intent = Intent(this, SleepActivity::class.java).apply {
                putExtra("petImageResId", petImageResId)
                putExtra("characterType", characterType)
                putExtra("petName", petName)
                putExtra("health", health)
                putExtra("mood", mood)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            Toast.makeText(this, "저장하기 기능은 아직 구현 중입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startImageAnimation() {
        animationHandler = Handler(Looper.getMainLooper())
        animationRunnable = object : Runnable {
            override fun run() {
                val frame = if (isFirstFrame) frame1Res else frame2Res
                petImageView.setImageResource(frame)
                isFirstFrame = !isFirstFrame
                animationHandler.postDelayed(this, 500)
            }
        }
        animationHandler.post(animationRunnable)
    }

    private fun updateStatusBars() {
        healthBar.progress = health
        moodBar.progress = mood
    }

    private fun startAutoDecrease() {
        launch {
            while (isActive) {
                delay(3000L)
                if (health > 0) health--
                if (mood > 0) mood--
                updateStatusBars()
            }
        }
    }

    private fun requestWeatherWithLocation() {
        WeatherUtil.getCurrentLocation(this) { location ->
            if (location != null) {
                Log.d("LocationDebug", "lat=${location.latitude}, lon=${location.longitude}")
                val grid = WeatherUtil.convertToGrid(location.latitude, location.longitude)
                fetchWeather(grid.x, grid.y)
            } else {
                Log.w("LocationDebug", "location is null!")
                findViewById<TextView>(R.id.speechBubble).apply {
                    text = "위치 정보를 가져올 수 없어요 😢"
                    visibility = View.VISIBLE
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("Permission", "GrantResult: ${grantResults.joinToString()}")
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestWeatherWithLocation()
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getBaseTime(): String {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)

        return when {
            hour < 2 || (hour == 2 && minute < 10) -> "2300"
            hour < 5 || (hour == 5 && minute < 10) -> "0200"
            hour < 8 || (hour == 8 && minute < 10) -> "0500"
            hour < 11 || (hour == 11 && minute < 10) -> "0800"
            hour < 14 || (hour == 14 && minute < 10) -> "1100"
            hour < 17 || (hour == 17 && minute < 10) -> "1400"
            hour < 20 || (hour == 20 && minute < 10) -> "1700"
            hour < 23 || (hour == 23 && minute < 10) -> "2000"
            else -> "2300"
        }
    }


    private fun fetchWeather(nx: Int, ny: Int) {
        val apiKey = "qvAMhThL7ZEBL+V4L8GMLNX+yH8QaeAhHh6GZKBdRqjcC8nI0xhml0pdcKR9QBdn3xfkl+x0Ow+dLCl5wcubig=="
        URLEncoder.encode(apiKey, "UTF-8")
        val baseDate = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(Date())
        val baseTime = getBaseTime()
        val url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" +
                "?serviceKey=${URLEncoder.encode(apiKey, "UTF-8")}" +
                "&numOfRows=10&pageNo=1&dataType=JSON&base_date=$baseDate&base_time=$baseTime&nx=$nx&ny=$ny"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        Log.d("WeatherURL", url)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@GameActivity, "날씨 정보를 가져오지 못했어요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body?.string() ?: throw Exception("응답 없음")
                    val json = JSONObject(body)
                    val response = json.getJSONObject("response")
                    val header = response.getJSONObject("header")
                    val resultCode = header.getString("resultCode")

                    if (resultCode != "00") {
                        val message = header.getString("resultMsg")
                        Log.e("WeatherError", "API 실패: $message")
                        runOnUiThread {
                            speechBubble.text = "날씨 정보를 받아오지 못했어요 😢\n($message)"
                            speechBubble.visibility = View.VISIBLE
                        }
                        return
                    }

                    val items = response
                        .getJSONObject("body")
                        .getJSONObject("items")
                        .getJSONArray("item")

                    var weather = "날씨 정보 없음"

                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val category = item.getString("category")
                        if (category == "PTY") {
                            val fcstValue = item.getString("fcstValue")
                            weather = when (fcstValue) {
                                "0" -> "맑아요 ☀️"
                                "1" -> "비가 와요 ☔️"
                                "2" -> "비/눈 ❄️"
                                "3" -> "눈이 와요 ⛄️"
                                else -> "날씨 정보를 알 수 없어요"
                            }
                            break
                        }
                    }

                    runOnUiThread {
                        speechBubble.text = weather
                        speechBubble.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Log.e("WeatherError", "날씨 응답 처리 실패: ${e.message}")
                    runOnUiThread {
                        speechBubble.text = "날씨 정보를 받아오지 못했어요 😢"
                        speechBubble.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("GameDebug", "GameActivity onDestroy 호출됨")
        animationHandler.removeCallbacks(animationRunnable)
        cancel()
    }
}
