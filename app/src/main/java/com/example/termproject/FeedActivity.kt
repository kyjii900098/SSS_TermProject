package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import android.provider.MediaStore
import android.app.Activity
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class FeedActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var selectBtn: Button
    private lateinit var cameraBtn: Button
    private lateinit var feedBtn: Button
    private var characterType : String = "sanjini"
    private var imageBitmap: Bitmap? = null

    private var petImageResId: Int = R.drawable.sanjini
    private var petName: String = "펫"
    private var health: Int = 50
    private var mood: Int = 50

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        imageView = findViewById(R.id.foodImageView)
        selectBtn = findViewById(R.id.selectImageButton)
        cameraBtn = findViewById(R.id.cameraImageButton)
        feedBtn = findViewById(R.id.feedConfirmButton)

        petImageResId = intent.getIntExtra("petImageResId", R.drawable.sanjini)
        petName = intent.getStringExtra("petName") ?: "펫"
        health = intent.getIntExtra("health", 50)
        mood = intent.getIntExtra("mood", 50)
        characterType = intent.getStringExtra("characterType") ?: "sanjini"

        // 📷 카메라 촬영
        cameraBtn.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }

        // 📁 갤러리 선택
        selectBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        // 🍴 먹이주기
        feedBtn.setOnClickListener {
            imageBitmap?.let { bitmap ->
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

                labeler.process(inputImage)
                    .addOnSuccessListener { labels ->
                        val result = if (labels.isNotEmpty()) labels[0].text else "알 수 없음"
                        Toast.makeText(this, "$result 먹였어요!", Toast.LENGTH_SHORT).show()

                        val resultIntent = Intent(this, GameActivity::class.java)
                        resultIntent.putExtra("sleepMode", false)
                        resultIntent.putExtra("sleepDuration", 0)
                        resultIntent.putExtra("petImageResId", petImageResId)
                        resultIntent.putExtra("petName", petName)
                        resultIntent.putExtra("health", health + 20)
                        resultIntent.putExtra("mood", mood)
                        resultIntent.putExtra("fedFood", result)
                        resultIntent.putExtra("characterType", characterType)
                        startActivity(resultIntent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "음식 인식 실패", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // 이미지 선택 or 촬영 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val uri = data?.data ?: return
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    imageBitmap = bitmap
                    imageView.setImageBitmap(bitmap)
                }
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap = bitmap
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }
}
