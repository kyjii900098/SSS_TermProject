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
            goToPetNameActivity(R.drawable.bird_frame1, "sanjini")
        }
        pet2Button.setOnClickListener {
            goToPetNameActivity(R.drawable.cow_frame1, "hobanu")
        }
        pet3Button.setOnClickListener {
            goToPetNameActivity(R.drawable.horse_frame1, "chacha")
        }
    }

    private fun goToPetNameActivity(imageResId: Int, characterType: String) {
        val intent = Intent(this, PetNameActivity::class.java)
        intent.putExtra("petImageResId", imageResId)
        intent.putExtra("characterType", characterType)
        startActivity(intent)
    }
}
