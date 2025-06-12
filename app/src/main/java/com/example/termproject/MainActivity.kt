    package com.example.termproject

    import android.content.Intent
    import android.os.Bundle
    import android.widget.Button
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat

    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main) // 여기는 그대로 main layout

            // 시스템 바 여백 조정
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            val startButton = findViewById<Button>(R.id.startButton)
            startButton.setOnClickListener {
                val intent = Intent(this, SelectPetActivity::class.java)
                startActivity(intent)
            }
            findViewById<Button>(R.id.exitButton).setOnClickListener {
                finishAffinity()
            }

            findViewById<Button>(R.id.continueButton).setOnClickListener {
                val prefs = getSharedPreferences("GameData", MODE_PRIVATE)

                val petImageResId = prefs.getInt("petImageResId", R.drawable.bird_frame1)
                val characterType = prefs.getString("characterType", "sanjini") ?: "sanjini"
                val petName = prefs.getString("petName", "이름 없음") ?: "이름 없음"
                val health = prefs.getInt("health", 80)
                val mood = prefs.getInt("mood", 70)

                val intent = Intent(this, GameActivity::class.java).apply {
                    putExtra("petImageResId", petImageResId)
                    putExtra("characterType", characterType)
                    putExtra("petName", petName)
                    putExtra("health", health)
                    putExtra("mood", mood)
                }

                startActivity(intent)
            }
        }
    }
