package io.github.nvlad1.function3danimator.ui.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.nvlad1.function3danimator.PersistHelper
import io.github.nvlad1.function3danimator.databinding.BasicComposeActivityBinding
import io.github.nvlad1.function3danimator.ui.theme.Function3dAnimatorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Created by Naboka Vladislav on 21.01.2018.
 */
@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private val gridSizeMax = 500
    private lateinit var binding: BasicComposeActivityBinding
    @Inject lateinit var persistHelper: PersistHelper
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BasicComposeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        enableEdgeToEdge()
        setContent {
            Function3dAnimatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingsScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
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