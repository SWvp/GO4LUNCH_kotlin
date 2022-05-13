package com.kardabel.go4lunch.presentation.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.databinding.ChatActivityBinding
import com.kardabel.go4lunch.di.ViewModelFactory
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ChatActivityBinding
    private lateinit var viewModel: ChatViewModel

    companion object {
        const val WORKMATE_ID = "WORKMATE_ID"
        const val WORKMATE_NAME = "WORKMATE_NAME"
        const val WORKMATE_PHOTO = "WORKMATE_PHOTO"
        fun navigate(
            context: Context,
            workmateId: String,
            workmateName: String,
            workmatePhoto: String,
        ): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(WORKMATE_ID, workmateId)
            intent.putExtra(WORKMATE_NAME, workmateName)
            intent.putExtra(WORKMATE_PHOTO, workmatePhoto)
            return intent
        }

        fun hideSoftKeyboard(activity: Activity?) {
            if (activity == null) return
            if (activity.currentFocus == null) return
            val inputMethodManager =
                activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageViewModel()
        managerAdapter()
        manageView()

    }

    private fun manageViewModel() {
        val viewModelFactory = ViewModelFactory.instance
        viewModel =
            ViewModelProvider(this, viewModelFactory!!)[ChatViewModel::class.java]

        intent.getStringExtra(WORKMATE_ID)?.let { viewModel.init(it) }
    }

    private fun managerAdapter() {
        binding.activityChatRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = ChatAdapter (this)
        binding.activityChatRecyclerView.adapter = adapter

        viewModel.getChatMessagesMediatorLiveData.observe(this) { chatMessages ->
            adapter.submitList(chatMessages)
            // MOVE TO THE LATEST MESSAGE
            binding.activityChatRecyclerView.scrollToPosition(chatMessages.size - 1)
        }
    }

    private fun manageView() {
        // FEED THE VIEW WITH MATE INFORMATION
        binding.mateName.text = intent.getStringExtra(WORKMATE_NAME)
        Glide.with(binding.workmatePhoto.context)
            .load(intent.getStringExtra(WORKMATE_PHOTO))
            .circleCrop()
            .into(binding.workmatePhoto)
        binding.backButton.setBackgroundColor(Color.parseColor(getString(R.string.back_button_color)))

        binding.activityChatSendButton.setOnClickListener { v ->
            if (!Objects.requireNonNull(binding.chatMessageEditText.text).toString()
                    .isEmpty()
            ) {
                val message = binding.chatMessageEditText.text.toString()
                viewModel.createChatMessage(message,
                    intent.getStringExtra(WORKMATE_ID)!!)
                binding.chatMessageEditText.text!!.clear()
                hideSoftKeyboard(this)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.type_your_text),
                    Toast.LENGTH_SHORT).show()
            }
        }
        binding.backButton.setOnClickListener { onBackPressed() }
    }
}