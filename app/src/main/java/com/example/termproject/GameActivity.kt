package com.example.termproject

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var healthBar: ProgressBar
    private lateinit var moodBar: ProgressBar
    private lateinit var petNameText: TextView
    private lateinit var petImageView: ImageView

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

        petNameText.text = "이름: $petName"
        petImageView.setImageResource(petImageResId)
        healthBar.progress = health
        moodBar.progress = mood

        findViewById<Button>(R.id.talkButton).setOnClickListener {
            mood = (mood + 10).coerceAtMost(100)
            moodBar.progress = mood
        }

        findViewById<Button>(R.id.feedButton).setOnClickListener {
            health = (health + 10).coerceAtMost(100)
            healthBar.progress = health
        }

        findViewById<Button>(R.id.sleepButton).setOnClickListener {
            mood = (mood + 5).coerceAtMost(100)
            health = (health + 5).coerceAtMost(100)
            moodBar.progress = mood
            healthBar.progress = health
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            // TODO: 저장 로직 구현 (SharedPreferences 등)
        }
    }
}
