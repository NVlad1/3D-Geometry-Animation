package io.github.nvlad1.function3danimator.ui.function_screen

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.nvlad1.function3danimator.R
import io.github.nvlad1.function3danimator.databinding.BasicComposeActivityBinding
import io.github.nvlad1.function3danimator.database.FunctionRepository
import io.github.nvlad1.function3danimator.model.FunctionModel
import io.github.nvlad1.function3danimator.ui.theme.Function3dAnimatorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FunctionActivityCompose : AppCompatActivity() {
    @Inject
    lateinit var functionRepository: FunctionRepository
    private val viewModel by viewModels<FunctionViewModelCompose>()
    private lateinit var binding: BasicComposeActivityBinding
    private lateinit var mFunction: FunctionModel
    private var functionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BasicComposeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (!resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        initFunction()
        enableEdgeToEdge()
        setContent {
            Function3dAnimatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FunctionScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    private fun initFunction() {
        functionId = intent.getStringExtra(EXTRA_FUNCTION_ID)
        viewModel.loadFunction(functionId)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onPause() {
        super.onPause()
        viewModel.save()
    }

    companion object {
        const val EXTRA_FUNCTION_ID = "Function3DAnimator.FUNCTION_ID"
    }
}
