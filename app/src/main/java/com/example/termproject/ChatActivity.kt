package com.example.termproject

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.termproject.adapter.ChatAdapter
import com.example.termproject.model.ChatMessage
import kotlinx.coroutines.*
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Toast
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.content.Intent

class ChatActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var characterType: String

    private val chatMessages = mutableListOf<ChatMessage>()

    private val database = FirebaseDatabase.getInstance().reference
    private val userId = "demo_user" // 추후 로그인 연동 시 교체

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        recyclerView = findViewById(R.id.chatRecyclerView)


        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        characterType = intent.getStringExtra("characterType") ?: "sanjini"
        val health = intent.getIntExtra("health", 80) // 기본값 80
        val mood = intent.getIntExtra("mood", 70)     // 기본값 70
        val petName = intent.getStringExtra("petName") ?: "name"

        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                addMessage(message, isUser = true)
                messageInput.text.clear()
                hideKeyboard()
                fetchCharacterReply(message)
            }
        }

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("sleepMode", true)
            intent.putExtra("characterType", characterType)
            //intent.putExtra("petImageResId", petImageResId)
            intent.putExtra("health", health)
            intent.putExtra("mood", mood)
            intent.putExtra("petName", petName)
            startActivity(intent)
            finish()
        }


        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendButton.performClick()
                true
            } else false
        }
        //loadChatHistory() // ✅ Firebase에서 기존 대화 불러오기
    }

    private fun saveMessageToFirebase(message: ChatMessage) {
        database.child("chats").child(userId).push().setValue(message)
    }

    private fun addMessage(text: String, isUser: Boolean) {
        val message = ChatMessage(text, isUser)
        chatMessages.add(message)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        recyclerView.scrollToPosition(chatMessages.size - 1)

        database.child("chats").child(userId).push().setValue(message)
        saveMessageToFirebase(message)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(messageInput.windowToken, 0)
    }

    private fun fetchCharacterReply(message: String) {
        launch {
            try {
                val response = ChatGPTService.getResponse(message, characterType)
                Log.d("ChatDebug", "GPT 응답: $response")

                chatMessages.add(ChatMessage(response, isUser = false))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                recyclerView.scrollToPosition(chatMessages.size - 1)

            } catch (e: Exception) {
                Log.e("ChatDebug", "응답 실패: ${e.message}")
                chatMessages.add(ChatMessage("응답 실패: ${e.message}", isUser = false))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                recyclerView.scrollToPosition(chatMessages.size - 1)
            }
        }
    }

    private fun ChatAdapter.addMessage(message: ChatMessage) {}

    private fun loadChatHistory() {
        database.child("chats").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatMessages.clear()
                    for (child in snapshot.children) {
                        val message = child.getValue(ChatMessage::class.java)
                        if (message != null) {
                            chatMessages.add(message)
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(chatMessages.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "불러오기 실패: ${error.message}")
                }
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
