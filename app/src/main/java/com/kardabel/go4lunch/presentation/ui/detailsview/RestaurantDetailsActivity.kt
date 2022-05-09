package com.kardabel.go4lunch.presentation.ui.detailsview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.databinding.RestaurantDetailsBinding
import com.kardabel.go4lunch.di.ViewModelFactory

class RestaurantDetailsActivity : AppCompatActivity() {

    private lateinit var binding: RestaurantDetailsBinding
    private lateinit var viewModel: RestaurantDetailsViewModel

    companion object {
        const val RESTAURANT_ID = "RESTAURANT_ID"
        fun navigate(context: Context, placeId: String): Intent {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra(RESTAURANT_ID, placeId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RestaurantDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageViewModel()
        managerAdapter()
        manageView()
    }

    private fun manageViewModel() {

        val viewModelFactory = ViewModelFactory.getInstance()
        viewModel =
            ViewModelProvider(this, viewModelFactory)[RestaurantDetailsViewModel::class.java]

        intent.getStringExtra(RESTAURANT_ID)?.let { viewModel.init(it) }
    }

    private fun managerAdapter() {

        binding.detailRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = RestaurantDetailsRecyclerViewAdapter {

        }
        binding.detailRecyclerView.adapter = adapter

        viewModel.workmatesLikeThisRestaurantMediatorLiveData.observe(this) { workmates ->
            adapter.submitList(workmates)
        }

    }

    private fun manageView() {

        var detailsViewState: RestaurantDetailsViewState? = null


        viewModel.restaurantDetailsViewStateMediatorLiveData.observe(this) { details ->

            detailsViewState = details

            binding.detailRestaurantName.text = details.detailsRestaurantName
            binding.detailRestaurantAddress.text = details.detailsRestaurantAddress
            Glide.with(binding.detailPicture.context)
                .load(details.detailsPhoto)
                .into(binding.detailPicture)
            binding.detailsRating.rating = details.rating.toFloat()
            binding.choseRestaurantButton.setImageResource(details.choseRestaurantButton)
            binding.choseRestaurantButton.setColorFilter(details.backgroundColor)
            binding.detailLikeButton.setImageResource(details.detailLikeButton)
        }

        binding.choseRestaurantButton.setOnClickListener {

            viewModel.onChoseRestaurantButtonClick(
                detailsViewState!!.detailsRestaurantId!!,
                detailsViewState!!.detailsRestaurantName!!,
                detailsViewState!!.detailsRestaurantAddress!!)

        }

        binding.detailLikeButton.setOnClickListener {
            detailsViewState.let {
                viewModel.onFavoriteIconClick(
                    detailsViewState!!.detailsRestaurantId!!,
                    detailsViewState!!.detailsRestaurantName!!)
            }
        }

        binding.callIcon.setOnClickListener {
            detailsViewState?.let {
                when (detailsViewState!!.detailsRestaurantNumber) {
                    R.string.no_phone_number.toString() ->
                        Toast.makeText(this,
                            getString(R.string.no_phone_number_message),
                            Toast.LENGTH_SHORT).show()
                    else -> {
                        startActivity(
                            Intent(Intent.ACTION_DIAL,
                                Uri.parse(getString(R.string.tel) + detailsViewState!!.detailsRestaurantNumber))
                        )
                    }
                }
            }
        }

        binding.webIcon.setOnClickListener {
            detailsViewState?.let {
                when (detailsViewState!!.detailsWebsite) {
                    R.string.google.toString() ->
                        Toast.makeText(this, getString(R.string.no_website), Toast.LENGTH_LONG)
                            .show()
                    else -> startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse(detailsViewState!!.detailsWebsite)))

                }
            }
        }
    }
}