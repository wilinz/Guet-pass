package com.wilinz.guet_pass.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wilinz.guet_pass.R
import com.wilinz.guet_pass.ui.theme.GuetpassTheme

class ImagePreviewActivity : ComponentActivity() {

    companion object {
        const val KEY_URIS = "uris"

        fun getIntent(context: Context): Intent {
            return Intent(context, ImagePreviewActivity::class.java)
        }

        fun start(context: Context) {
            context.startActivity(getIntent(context))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setContent {
            GuetpassTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    SetSystemUi()
                    val context = LocalContext.current
                    Box(
                        Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (context is Activity) context.finish()
                                    }
                                )
                            }
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = R.drawable.fanzha),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "1/1", color = Color.White)
                        }
                    }

                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes = window.attributes.apply {
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }

            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = ViewCompat.getWindowInsetsController(window.decorView)
            controller?.hide(WindowInsetsCompat.Type.systemBars())
            controller?.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }
    }

    @Composable
    private fun SetSystemUi() {
        // Remember a SystemUiController
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = !isSystemInDarkTheme()

        DisposableEffect(systemUiController, useDarkIcons) {
            // Update all of the system bar colors to be transparent, and use
            // dark icons if we're in light theme
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = false
            )

            // setStatusBarColor() and setNavigationBarColor() also exist

            onDispose {}
        }

    }
}

