package io.github.nvlad1.function3danimator.ui.functions_list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import io.github.nvlad1.function3danimator.ui.function_screen.FunctionActivityCompose
import io.github.nvlad1.function3danimator.ui.settings.SettingsActivity
import io.github.nvlad1.function3danimator.ui.theme.Function3dAnimatorTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Naboka Vladislav on 06.12.2017.
 */
@AndroidEntryPoint
class FunctionListActivity : AppCompatActivity() {
    private val viewModel by viewModels<FunctionListViewModel>()
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
                    FunctionListScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateFunctions()
    }
    override fun onPause() {
        super.onPause()
        viewModel.saveFunctionsToFile()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_function_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_add -> {
                if (viewModel.maxFunctionsReached){
                    Toast.makeText(this, R.string.max_function_number_reached, Toast.LENGTH_SHORT).show()
                } else {
                    val i = Intent(this, FunctionActivityCompose::class.java)
                    startActivity(i)
                }
                true
            }
            R.id.menu_item_settings -> {
                val i1 = Intent(this, SettingsActivity::class.java)
                startActivity(i1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}