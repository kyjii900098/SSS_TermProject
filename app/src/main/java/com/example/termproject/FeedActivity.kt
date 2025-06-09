package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.ObjectDetection
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import android.provider.MediaStore
import android.app.Activity

class FeedActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var selectBtn: Button
    private lateinit var feedBtn: Button
    private var imageBitmap: Bitmap? = null
    private lateinit var objectDetector: ObjectDetector

    // 전달받을 정보
    private var petImageResId: Int = R.drawable.sanjini
    private var petName: String = "펫"
    private var health: Int = 50
    private var mood: Int = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        imageView = findViewById(R.id.foodImageView)
        selectBtn = findViewById(R.id.selectImageButton)
        feedBtn = findViewById(R.id.feedConfirmButton)

        // 전달받은 인텐트 값 추출
        petImageResId = intent.getIntExtra("petImageResId", R.drawable.sanjini)
        petName = intent.getStringExtra("petName") ?: "펫"
        health = intent.getIntExtra("health", 50)
        mood = intent.getIntExtra("mood", 50)

        // ML Kit 객체 탐지기 설정
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        objectDetector = ObjectDetection.getClient(options)

        // 이미지 선택
        selectBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        // 먹이 주기 버튼
        feedBtn.setOnClickListener {
            imageBitmap?.let { bitmap ->
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                objectDetector.process(inputImage)
                    .addOnSuccessListener { objects ->
                        var result = "알 수 없음"
                        for (obj in objects) {
                            for (label in obj.labels) {
                                result = label.text
                                break
                            }
                        }
                        Toast.makeText(this, "$result 먹였어요!", Toast.LENGTH_SHORT).show()

                        // ▶ GameActivity로 정보 전달
                        val resultIntent = Intent(this, GameActivity::class.java)
                        resultIntent.putExtra("sleepMode", false) // 수면 모드 아님
                        resultIntent.putExtra("sleepDuration", 0) // 0분
                        resultIntent.putExtra("petImageResId", petImageResId)
                        resultIntent.putExtra("petName", petName)
                        resultIntent.putExtra("health", health)
                        resultIntent.putExtra("mood", mood)
                        resultIntent.putExtra("fedFood", result)
                        startActivity(resultIntent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "인식 실패", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // 이미지 선택 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            imageBitmap = bitmap
            imageView.setImageBitmap(bitmap)
        }
    }
}