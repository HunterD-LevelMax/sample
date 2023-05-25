package com.template.sample.activity

import android.os.Bundle
import com.template.sample.customclass.CustomActivity
import com.template.sample.databinding.ActivityGameBinding

class GameActivity : CustomActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}