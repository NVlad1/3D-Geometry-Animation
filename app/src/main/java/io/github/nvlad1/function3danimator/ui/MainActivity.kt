package io.github.nvlad1.function3danimator.ui

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.Crossfade
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import io.github.nvlad1.function3danimator.PersistHelper
import io.github.nvlad1.function3danimator.R
import io.github.nvlad1.function3danimator.model.DefaultFunctionSet
import io.github.nvlad1.function3danimator.openGLutils.GLSurfaceViewWithRotation
import io.github.nvlad1.function3danimator.ui.functions_list.FunctionListActivity
import io.github.nvlad1.function3danimator.ui.help.HelpActivity
import io.github.nvlad1.function3danimator.ui.settings.EnumMainActivityOrientation
import io.github.nvlad1.function3danimator.ui.settings.SettingsActivity
import io.github.nvlad1.function3danimator.ui.theme.Function3dAnimatorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var persistHelper: PersistHelper
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var glSurfaceView: GLSurfaceViewWithRotation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                ContextCompat.getColor(this, R.color.colorBlack)
            )
        )
        if (!supportES2()) {
            Toast.makeText(this, "OpenGL ES 2.0 is not supported", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        setContent {
            Function3dAnimatorTheme {
                val uiState = viewModel.uiState
                OpenGlSurfaceScreen(
                    uiState = uiState,
                    onSelectExample = { example ->
                        if (::glSurfaceView.isInitialized) {
                            glSurfaceView.onPause()
                        }
                        viewModel.selectExample(example)
                    },
                    onOpenFunctionsList = {
                        startActivity(Intent(this, FunctionListActivity::class.java))
                    },
                    onOpenSettings = {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    },
                    onOpenHelp = {
                        startActivity(Intent(this, HelpActivity::class.java))
                    },
                    createGlSurfaceView = { state ->
                        GLSurfaceViewWithRotation(
                            this,
                            persistHelper.getShowGridPref(),
                            state
                        ).also { glSurfaceView = it }
                    }
                )
            }
        }
        title = ""
    }

    override fun onPause() {
        super.onPause()
        if (::glSurfaceView.isInitialized) {
            glSurfaceView.onPause()
        }
        viewModel.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        specifyOrientation()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (::glSurfaceView.isInitialized) {
            glSurfaceView.onResume()
        }
        viewModel.onResume()
    }

    private fun specifyOrientation() {
        when (persistHelper.getMainScreenOrientationPref()) {
            EnumMainActivityOrientation.portrait ->
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            EnumMainActivityOrientation.landscape ->
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun supportES2(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        return configurationInfo.reqGlEsVersion >= 0x20000
    }
}

@Composable
private fun OpenGlSurfaceScreen(
    uiState: MainUiState,
    onSelectExample: (DefaultFunctionSet) -> Unit,
    onOpenFunctionsList: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit,
    createGlSurfaceView: (io.github.nvlad1.function3danimator.openGLutils.FunctionRenderState) -> GLSurfaceViewWithRotation
) {
    var isDrawerOpen by remember { mutableStateOf(false) }

    BackHandler(enabled = isDrawerOpen) {
        isDrawerOpen = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Scaffold(
            topBar = {
                MainTopBar(
                    onOpenDrawer = {
                        isDrawerOpen = true
                    },
                    onOpenFunctionsList = onOpenFunctionsList,
                    onOpenSettings = onOpenSettings,
                    onOpenHelp = onOpenHelp
                )
            },
            backgroundColor = Color.Transparent
        ) { innerPadding ->
            MainContent(
                modifier = Modifier.padding(innerPadding),
                renderState = uiState.renderState,
                renderVersion = uiState.renderVersion,
                showTimer = uiState.showTimer,
                timerText = uiState.timerText,
                createGlSurfaceView = createGlSurfaceView
            )
        }

        if (isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.32f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isDrawerOpen = false
                    }
            )

            Box(
                modifier = Modifier
                    .width(304.dp)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
            ) {
                MainDrawerContent(
                    onItemClick = { example ->
                        onSelectExample(example)
                        isDrawerOpen = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MainTopBar(
    onOpenDrawer: () -> Unit,
    onOpenFunctionsList: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit
) {
    TopAppBar(
        title = { Text(text = "") },
        backgroundColor = colorResource(R.color.colorTransparent),
        contentColor = Color.White,
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.navigation_drawer_open)
                )
            }
        },
        actions = {
            IconButton(onClick = onOpenFunctionsList) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_add),
                    contentDescription = stringResource(R.string.functions_list)
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings_white_24dp),
                    contentDescription = stringResource(R.string.settings)
                )
            }
            IconButton(onClick = onOpenHelp) {
                Icon(
                    painter = painterResource(R.drawable.ic_help_white_24dp),
                    contentDescription = stringResource(R.string.help)
                )
            }
        },
        elevation = 4.dp
    )
}

@Composable
private fun MainDrawerContent(
    onItemClick: (DefaultFunctionSet) -> Unit
) {
    val drawerItems = listOf(
        DrawerItem(DefaultFunctionSet.default, R.string.menu_example1),
        DrawerItem(DefaultFunctionSet.pulsating_sphere, R.string.menu_example2),
        DrawerItem(DefaultFunctionSet.bubble, R.string.menu_example3),
        DrawerItem(DefaultFunctionSet.waves, R.string.menu_example4),
        DrawerItem(DefaultFunctionSet.plate, R.string.menu_example5),
        DrawerItem(DefaultFunctionSet.gaussian, R.string.menu_example6),
        DrawerItem(DefaultFunctionSet.spinner, R.string.menu_example7),
        DrawerItem(DefaultFunctionSet.cylinder, R.string.menu_example8),
        DrawerItem(DefaultFunctionSet.moebius_strip, R.string.menu_example9)
    )
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.DarkGray)
    ) {
        MainDrawerHeader()
        drawerItems.forEach { item ->
            val interactionSource = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current
                    ) { onItemClick(item.example) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(item.titleRes),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun MainDrawerHeader() {
    val horizontalPadding = dimensionResource(R.dimen.activity_horizontal_margin)
    val verticalPadding = dimensionResource(R.dimen.activity_vertical_margin)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.nav_header_height))
            .background(Color.DarkGray)
            .padding(
                start = horizontalPadding,
                top = verticalPadding,
                end = horizontalPadding,
                bottom = verticalPadding
            ),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = stringResource(R.string.nav_header_title),
            color = Color.White,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.White)
        )
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    renderState: io.github.nvlad1.function3danimator.openGLutils.FunctionRenderState?,
    renderVersion: Int,
    showTimer: Boolean,
    timerText: String,
    createGlSurfaceView: (io.github.nvlad1.function3danimator.openGLutils.FunctionRenderState) -> GLSurfaceViewWithRotation
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Crossfade(targetState = renderVersion, label = "render_crossfade") { version ->
            renderState?.let { state ->
                key(version) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { createGlSurfaceView(state) }
                    )
                }
            }
        }
        if (showTimer) {
            Text(
                text = timerText,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

private data class DrawerItem(
    val example: DefaultFunctionSet,
    val titleRes: Int
)
