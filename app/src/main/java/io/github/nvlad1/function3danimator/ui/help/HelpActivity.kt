package io.github.nvlad1.function3danimator.ui.help

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.text.style.UnderlineSpan
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.nvlad1.function3danimator.R
import io.github.nvlad1.function3danimator.databinding.BasicComposeActivityBinding
import io.github.nvlad1.function3danimator.ui.settings.SettingsScreen
import io.github.nvlad1.function3danimator.ui.theme.Function3dAnimatorTheme


class HelpActivity : AppCompatActivity() {
    private lateinit var binding: BasicComposeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BasicComposeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        enableEdgeToEdge()
        setContent {
            Function3dAnimatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HelpScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}