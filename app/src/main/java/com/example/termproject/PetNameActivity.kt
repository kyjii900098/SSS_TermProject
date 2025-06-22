package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class PetNameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_name)

        val petImageResId = intent.getIntExtra("petImageResId", 0)
        val characterType = intent.getStringExtra("characterType") ?: "sanjini"

        val petImage = findViewById<ImageView>(R.id.petImageView)
        val nameInput = findViewById<EditText>(R.id.petNameInput)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val descriptionText = findViewById<android.widget.TextView>(R.id.characterDescription)

        petImage.setImageResource(petImageResId)

        // ✅ characterType에 따라 설명 텍스트 설정
        val description = when (characterType) {
            "sanjini" -> "산지니는 부산대학교의 마스코트 캐릭터입니다\n똑똑하고 차분한 성격을 가지고 있어요"
            "hobanu" -> "호반우는 경북대학교의 마스코트 캐릭터입니다\n듬직하고 신중한 성격을 가지고 있어요"
            "chacha" -> "차차는 충남대학교의 마스코트 캐릭터입니다\n활기차고 귀여운 성격을 가지고 있어요 "
            else -> "귀여운 친구가 여러분을 기다리고 있어요!"
        }
        descriptionText.text = description

        confirmButton.setOnClickListener {
            val petName = nameInput.text.toString()
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("petName", petName)
            intent.putExtra("petImageResId", petImageResId)
            intent.putExtra("characterType", characterType)
            startActivity(intent)
            finish()
        }
    }
}