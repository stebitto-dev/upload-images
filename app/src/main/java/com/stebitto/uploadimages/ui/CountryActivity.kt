package com.stebitto.uploadimages.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stebitto.uploadimages.databinding.ActivityCountriesBinding
import com.stebitto.uploadimages.statemachines.AppStateMachine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CountryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountriesBinding

    @Inject lateinit var appStateMachine: AppStateMachine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCountriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appStateMachine.state.collect { state ->
                    Timber.d(state.toString())
                }
            }
        }
    }
}