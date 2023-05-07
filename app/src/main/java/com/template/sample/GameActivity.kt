package com.template.sample

import android.os.Bundle
import com.template.sample.databinding.ActivityGameBinding

class GameActivity : BaseActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}