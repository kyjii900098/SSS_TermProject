package com.example.termproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.termproject.R
import com.example.termproject.model.ChatMessage

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_CHARACTER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_CHARACTER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = layoutInflater.inflate(R.layout.item_chat_user, parent, false)
                UserViewHolder(view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_chat_character, parent, false)
                CharacterViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is CharacterViewHolder) {
            holder.characterMessageText.text = message.text
        } else if (holder is UserViewHolder) {
            holder.userMessageText.text = message.text
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userMessageText: TextView = itemView.findViewById(R.id.userMessageText)
    }

    inner class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val characterMessageText: TextView = itemView.findViewById(R.id.characterMessageText)
    }

}
