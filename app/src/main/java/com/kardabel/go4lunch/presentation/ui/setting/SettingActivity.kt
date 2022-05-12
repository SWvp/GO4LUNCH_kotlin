package com.kardabel.go4lunch.presentation.ui.setting

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.databinding.SettingsActivityBinding
import com.kardabel.go4lunch.di.ViewModelFactory
import com.kardabel.go4lunch.util.PermissionsViewAction

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageViewModel()

        binding.settingsToolbar.setOnClickListener { view -> onBackPressed() }
    }

    private fun manageViewModel() {
        val viewModelFactory = ViewModelFactory.getInstance()
        val viewModel = ViewModelProvider(this, viewModelFactory)[SettingViewModel::class.java]

        viewModel.getSwitchPosition.observe(this) { switchPosition ->

            when (switchPosition) {
                1 -> binding.switchNotification.isChecked = false
                2 -> binding.switchNotification.isChecked = true
            }
        }

        viewModel.actionSingleLiveEvent.observe(this) { permission ->
            when(permission){
                PermissionsViewAction.NOTIFICATION_DISABLED -> Toast.makeText(this, getString(R.string.settings_notification_disabled),
                    Toast.LENGTH_SHORT)
                    .show()
                PermissionsViewAction.NOTIFICATION_ENABLED -> Toast.makeText(this, getString(R.string.settings_notification_activated),
                    Toast.LENGTH_SHORT)
                    .show()
                else -> {}
            }
        }

        binding.switchNotification.setOnClickListener { viewModel.notificationChange() }
    }
}