package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class GameActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var healthBar: ProgressBar
    private lateinit var moodBar: ProgressBar
    private lateinit var petNameText: TextView
    private lateinit var petImageView: ImageView
    private lateinit var feedActivityLauncher: ActivityResultLauncher<Intent>

    private var health = 80
    private var mood = 70

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        healthBar = findViewById(R.id.healthBar)
        moodBar = findViewById(R.id.moodBar)
        petNameText = findViewById(R.id.petNameText)
        petImageView = findViewById(R.id.petImageView)

        val petName = intent.getStringExtra("petName") ?: "이름 없음"
        val petImageResId = intent.getIntExtra("petImageResId", R.drawable.sanjini)
        health = intent.getIntExtra("health", 80)
        mood = intent.getIntExtra("mood", 70)
        petNameText.text = "이름: $petName"
        petImageView.setImageResource(petImageResId)
        updateStatusBars()

        findViewById<Button>(R.id.talkButton).setOnClickListener {      //대화하기
            mood = (mood + 10).coerceAtMost(100)
            updateStatusBars()
        }
        val sleepMode = intent.getBooleanExtra("sleepMode", false)
        val speechBubble = findViewById<TextView>(R.id.speechBubble)
        val sleepDurationMin = intent.getIntExtra("sleepDuration", 0)

        if (sleepMode && sleepDurationMin > 0) {
            speechBubble.text = "zzz"
            speechBubble.visibility = View.VISIBLE
            startSleepRecovery(sleepDurationMin)
        } else {
            speechBubble.visibility = View.GONE
        }
        findViewById<Button>(R.id.feedButton).setOnClickListener {      //밥주기
            val intent = Intent(this, FeedActivity::class.java)
            // 펫 정보 전달 (필요시)
            intent.putExtra("petImageResId", petImageResId)
            intent.putExtra("petName", petName)
            intent.putExtra("health", health)
            intent.putExtra("mood", mood)
            startActivity(intent)
        }

        findViewById<Button>(R.id.sleepButton).setOnClickListener {     //잠자기
            val intent = Intent(this, SleepActivity::class.java)
            intent.putExtra("petImageResId", petImageResId)
            intent.putExtra("petName", petName)
            intent.putExtra("health", health)
            intent.putExtra("mood", mood)
            startActivity(intent)
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {      //저장하기
            Toast.makeText(this, "저장하기 기능은 아직 구현 중입니다.", Toast.LENGTH_SHORT).show()
        }

        val fedFood = intent.getStringExtra("fedFood")          // feed 받아오기
        if (fedFood != null) {
            health = (health + 5).coerceAtMost(100)
            updateStatusBars()
            Toast.makeText(this, "$fedFood 먹고 체력이 +5 회복됐어요!", Toast.LENGTH_SHORT).show()
        }

        startAutoDecrease()
    }





    private fun startSleepRecovery(minutes: Int) {
        val totalSeconds = minutes
        launch {
            repeat(totalSeconds) {
                delay(1000L) // 1초
                health = (health + 3).coerceAtMost(100)
                updateStatusBars()
            }
            // 수면 끝나면 말풍선 사라짐
            findViewById<TextView>(R.id.speechBubble).visibility = View.GONE
        }
    }

    private fun updateStatusBars() {
        healthBar.progress = health
        moodBar.progress = mood
    }

    private fun startAutoDecrease() {
        launch {
            while (isActive) {
                delay(3_000) // 10초마다
                if (health > 0) health--
                if (mood > 0) mood--
                updateStatusBars()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel() // 코루틴 정리
    }
}
