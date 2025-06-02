package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SelectPetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_pet)

        val pet1Button = findViewById<Button>(R.id.pet1Button)
        val pet2Button = findViewById<Button>(R.id.pet2Button)
        val pet3Button = findViewById<Button>(R.id.pet3Button)

        pet1Button.setOnClickListener {
            goToPetNameActivity(R.drawable.sanjini)
        }
        pet2Button.setOnClickListener {
            goToPetNameActivity(R.drawable.hobanu)
        }
        pet3Button.setOnClickListener {
            goToPetNameActivity(R.drawable.chacha)
        }
    }

    private fun goToPetNameActivity(imageResId: Int) {
        val intent = Intent(this, PetNameActivity::class.java)
        intent.putExtra("petImageResId", imageResId)
        startActivity(intent)
    }
}
