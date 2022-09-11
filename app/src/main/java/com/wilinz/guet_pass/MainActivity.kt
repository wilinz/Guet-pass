package com.wilinz.guet_pass

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wilinz.guet_pass.ui.theme.GuetpassTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    companion object {
        val format = SimpleDateFormat("HH:mm:ss:SS", Locale.CHINESE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            GuetpassTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SetSystemUi()
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            Image(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillBounds
                            )

                            TopAppBar()

                            var time by remember {
                                mutableStateOf("20:57:38:14")
                            }
                            LaunchedEffect(key1 = Unit, block = {
                                while (true) {
                                    delay(130)
                                    time = format.format(
                                        System.currentTimeMillis()
                                    )
                                }
                            })
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .padding(
                                            top = 115.dp,
                                            start = 15.dp,
                                            end = 16.dp
                                        )
                                        .fillMaxWidth()
                                        .background(color = Color.Transparent),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Spacer(modifier = Modifier.height(17.dp))
                                    Box(Modifier.fillMaxWidth(), Alignment.Center) {
                                        var width by remember {
                                            mutableStateOf(0)
                                        }
                                        Text(
                                            text = time,
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF08B906),
                                            modifier = Modifier.onSizeChanged { width = it.width },
                                            fontFamily = FontFamily.Default
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .width(with(LocalDensity.current) { width.toDp() })
                                                .background(Color.White)
                                                .height(9.5.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(51.dp))

                                    var uri by remember {
                                        val uri = Pref.headPath?.let { File(it) }?.toUri()
                                        mutableStateOf<Uri?>(uri)
                                    }
                                    val launcher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.GetContent(),
                                        onResult = {
                                            if (it == null) return@rememberLauncherForActivityResult
                                            uri = it
                                            val file = File(App.application.filesDir, "head.png")
                                            uri?.copyTo(App.application, file)
                                            Pref.headPath = file.absolutePath
                                        }
                                    )
                                    val context = LocalContext.current
                                    AsyncImage(
                                        model = if (uri == null) R.drawable.head else uri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .pointerInput(Unit) {
                                                detectTapGestures(onDoubleTap = {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "请选择头像",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                    launcher.launch("image/*")
                                                })
                                            }
                                    )

                                    Spacer(modifier = Modifier.height(9.5.dp))

                                    var isShowNameDialog by remember {
                                        mutableStateOf(false)
                                    }
                                    var lastName by remember {
                                        mutableStateOf(Pref.lastName)
                                    }
                                    Text(
                                        text = "**$lastName 可以通行",
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF008001),
                                        modifier = Modifier.pointerInput(Unit) {
                                            detectTapGestures(onDoubleTap = {
                                                isShowNameDialog = true
                                            })
                                        }
                                    )

                                    if (isShowNameDialog) {
                                        AlertDialog(onDismissRequest = {
                                            isShowNameDialog = false
                                        },
                                            title = {
                                                Text(text = "请输入名字最后一个字：")
                                            },
                                            text = {
                                                TextField(
                                                    value = lastName, onValueChange = {
                                                        lastName = it
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = TextFieldDefaults.textFieldColors(
                                                        backgroundColor = Color.Transparent
                                                    ),
                                                    placeholder = {
                                                        Text(text = "名字最后一个字")
                                                    }
                                                )
                                            },
                                            confirmButton = {
                                                TextButton(onClick = {
                                                    isShowNameDialog = false
                                                    Pref.lastName = lastName
                                                }) {
                                                    Text(text = "确定")
                                                }

                                            }
                                        )
                                    }

                                    Surface(
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(
                                                    top = 29.dp,
                                                )
                                                .background(color = Color.Transparent)
                                                .height(80.dp)
                                                .fillMaxWidth()
                                                .clickable {
                                                    ImagePreviewActivity.start(context = context)
                                                }
                                        )
                                    }

                                    var isShow by rememberSaveable {
                                        mutableStateOf(false)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 12.dp)
                                            .fillMaxWidth()
                                            .background(color = Color.White)
                                            .animateContentSize()
                                            .clickable {
                                                isShow = !isShow
                                            }
                                            .padding(
                                                top = 15.dp,
                                                bottom = 15.dp,
                                                start = 15.dp,
                                                end = 20.dp
                                            )

                                    ) {
                                        Column(Modifier.fillMaxWidth()) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(start = 5.dp)
                                            ) {
                                                Surface(
                                                    color = Color(0xFF07C160),
                                                    shape = RoundedCornerShape(2.dp)
                                                ) {
                                                    Box(
                                                        Modifier
                                                            .wrapContentSize()
                                                            .padding(
                                                                horizontal = 5.dp,
                                                                vertical = 3.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "已同意",
                                                            fontSize = 10.sp,
                                                            color = Color.White
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "桂电学生桂电学生临时通行证（备案制）",
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF323232)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(
                                                    painter = painterResource(id = if (isShow) R.drawable.ic_less else R.drawable.ic_more),
                                                    contentDescription = null,
                                                    Modifier.size(20.dp),
                                                    tint = Color(0xff949899)
                                                )
                                            }

                                            if (isShow) {
                                                Divider(
                                                    color = Color(0xFFf0f0f2),
                                                    modifier = Modifier.padding(
                                                        top = 13.dp,
                                                        bottom = 16.dp
                                                    )
                                                )
                                                Text(
                                                    text = "不限次数\n限制通行2次，当天有效，从首次通行开始，到当天22点或者从首次通行后2小时，取小值。每天 05:00~21:59 内可申请。",
                                                    color = Color(0xFF969799),
                                                    fontSize = 13.sp
                                                )
                                            }

                                        }


                                    }


                                }


                            }
                        }
                        BottomBar()
                    }

                }
            }
        }
    }

    @Composable
    private fun BottomBar() {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    scope.launch {
                        delay(1000)
                        Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show()
                    }
                },
                border = BorderStroke(0.5.dp, color = Color(0xFFECEDF1)),
                modifier = Modifier
                    .padding(start = 15.dp, end = 20.dp)
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF323232)
                ),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(text = "返回首页")
            }
            TextButton(
                onClick = {
                    scope.launch {
                        delay(1000)
                        Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .padding(start = 20.dp, end = 24.dp)
                    .weight(1f)
                    .height(44.dp),
                border = BorderStroke(0.5.dp, color = Color(0xFFECEDF1)),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF323232)
                ),
                shape = RoundedCornerShape(2.dp)
            ) {

                Text(text = "出行记录")
            }
        }
        Spacer(
            modifier = Modifier
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
        )
    }

    @Composable
    private fun TopAppBar() {
        Column {
            Spacer(
                modifier = Modifier
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )
            Row {
                Spacer(modifier = Modifier.weight(1f))
                val context = LocalContext.current
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    //                                        border = BorderStroke(0.01.dp, Color(133, 133, 133)),
                    modifier = Modifier
                        //                                            .padding(8.dp)
                        .wrapContentWidth(),
                    //                                            .height(32.dp),
                    color = Color(0x33000000)
                ) {
                    Row(
                        Modifier
                            .wrapContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .wrapContentSize()
                                .clickable {
                                    val intent = Intent();
                                    intent.action = Intent.ACTION_SEND;
                                    //                                    intent.data = uri
                                    intent.type = "text/plain";
                                    intent.putExtra(Intent.EXTRA_TEXT, "桂电畅行证")
                                    startActivity(intent);
                                }
                                .padding(vertical = 4.dp), Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wechat_more),
                                contentDescription = "更多",
                                Modifier
                                    .padding(horizontal = 16.dp)
                                    .size(20.dp)
                            )
                        }

                        Box() {
                            Box(
                                Modifier
                                    //                                                        .padding(vertical = 2.dp)
                                    .height(20.dp)
                                    .width(0.3.dp)
                                    .background(Color(229, 229, 229))
                            )
                        }

                        Box(
                            Modifier
                                .wrapContentSize()
                                .clickable {
                                    if (context is Activity) context.finish()
                                }
                                .padding(vertical = 4.dp), Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wechat_close),
                                contentDescription = "关闭",
                                Modifier
                                    .padding(horizontal = 16.dp)
                                    .size(20.dp)
                            )
                        }
                    }
                }

            }

        }
    }
}

@Composable
fun SetSystemUi() {
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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GuetpassTheme {
        Greeting("Android")
    }
}