package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SleepActivity : AppCompatActivity() {

    private var sleepMinutes = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)

        val petImageResId = intent.getIntExtra("petImageResId", R.drawable.sanjini)
        val petName = intent.getStringExtra("petName") ?: "이름 없음"
        val health = intent.getIntExtra("health", 80)
        val mood = intent.getIntExtra("mood", 70)

        val timeText = findViewById<TextView>(R.id.sleepTimeText)
        val minusBtn = findViewById<Button>(R.id.minusButton)
        val plusBtn = findViewById<Button>(R.id.plusButton)
        val confirmBtn = findViewById<Button>(R.id.confirmButton)
        val backBtn = findViewById<Button>(R.id.backButton)

        timeText.text = sleepMinutes.toString()

        minusBtn.setOnClickListener {
            if (sleepMinutes > 5) {
                sleepMinutes -= 5
                timeText.text = sleepMinutes.toString()
            }
        }

        plusBtn.setOnClickListener {
            sleepMinutes += 5
            timeText.text = sleepMinutes.toString()
        }

        confirmBtn.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("sleepMode", true)
            intent.putExtra("sleepDuration", sleepMinutes)  // ← 수면 시간(분)
            intent.putExtra("petImageResId", petImageResId)
            intent.putExtra("petName", petName)
            intent.putExtra("health", health)
            intent.putExtra("mood", mood)
            startActivity(intent)
            finish()
        }

        backBtn.setOnClickListener {
            finish()
        }
    }
}
