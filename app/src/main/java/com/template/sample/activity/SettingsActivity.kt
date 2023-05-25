package com.template.sample.activity

import android.os.Bundle
import com.template.sample.customclass.CustomActivity
import com.template.sample.databinding.ActivitySettingsBinding

class SettingsActivity : CustomActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}