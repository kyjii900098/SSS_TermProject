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
        val petImage = findViewById<ImageView>(R.id.petImageView)
        val nameInput = findViewById<EditText>(R.id.petNameInput)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val characterType = intent.getStringExtra("characterType") ?: "sanjini"

        petImage.setImageResource(petImageResId)

        confirmButton.setOnClickListener {
            val petName = nameInput.text.toString()
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("petName", petName)
            intent.putExtra("petImageResId", petImageResId)
            intent.putExtra("characterType", characterType)
            startActivity(intent)
            finish() // 👈 여기에!
        }
    }
}
