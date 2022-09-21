package com.wilinz.guet_pass.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wilinz.guet_pass.App
import com.wilinz.guet_pass.Pref
import com.wilinz.guet_pass.R
import com.wilinz.guet_pass.copyTo
import com.wilinz.guet_pass.ui.theme.GuetpassTheme
import com.wilinz.guet_pass.ui.widget.AutoResizeText
import com.wilinz.guet_pass.ui.widget.FontSizeRange
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
                            Background()
                            Foreground()
                        }
                        BottomBar()
                    }

                }
            }
        }
    }

    @Composable
    private fun Foreground() {
        var passTypeName by remember {
            mutableStateOf(Pref.passType)
        }
        TopAppBar()
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(
                        top = 107.dp,
                        start = 14.dp,
                        end = 15.dp
                    )
                    .fillMaxWidth()
                    .background(color = Color.Transparent),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PassCard(
                    passTypeName = passTypeName,
                    onPassTypeNameChange = { passTypeName = it;Pref.passType = it }
                )
                AntiFraud()
                PassInformation(passTypeName)
            }
        }
    }

    @Composable
    private fun Background() {
        Column(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.houjie),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
    }

    @Composable
    private fun PassCard(passTypeName: String, onPassTypeNameChange: (String) -> Unit) {

        Surface(shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(17.dp))
                Time()
                Spacer(modifier = Modifier.height(8.dp))
                Title(passTypeName, onPassTypeNameChange)
                Spacer(modifier = Modifier.height(5.dp))
                Avatar()
                Spacer(modifier = Modifier.height(9.5.dp))
                Name()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }

    @Composable
    private fun Name() {
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
    }

    @Composable
    private fun Avatar() {
        var uri by remember {
            val uri = Pref.headPath?.let { File(it) }?.toUri()
            mutableStateOf<Uri?>(uri)
        }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = {
                if (it == null) return@rememberLauncherForActivityResult
                uri = it
                val file =
                    File(App.application.filesDir, "head.png")
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
    }

    @OptIn(ExperimentalUnitApi::class)
    @Composable
    private fun Title(passTypeName: String, onPassTypeNameChange: (String) -> Unit) {
        var passTypeName1 by remember {
            mutableStateOf(passTypeName)
        }
        AutoResizeText(
            text = passTypeName,
            fontSizeRange = FontSizeRange(
                TextUnit(20f, TextUnitType.Sp),
                TextUnit(26f, TextUnitType.Sp)
            ),
            fontWeight = FontWeight.Black,
            color = Color(0xFF08B906),
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        passTypeName1 = if (passTypeName1 == "桂电学生临时通行证（备案制）") {
                            "桂电学生临时通行证"
                        } else {
                            "桂电学生临时通行证（备案制）"
                        }
                        Log.d("Title: ", passTypeName1)
                        Log.d("Title: ", passTypeName1)
                        onPassTypeNameChange(passTypeName1)
                    })
                }
        )
//        Image(
//            painter = painterResource(id = R.drawable.text1),
//            contentDescription = null,
//            contentScale = ContentScale.FillBounds,
//            modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)
//        )
    }

    @Composable
    private fun Time() {
        Box(Modifier.fillMaxWidth(), Alignment.Center) {
            var width by remember {
                mutableStateOf(0)
            }
            var isShow by remember {
                mutableStateOf(false)
            }


            var time by remember {
                mutableStateOf("20:57:38:14")
            }
            var interval by remember {
                mutableStateOf(130L)
            }
            LaunchedEffect(key1 = Unit, block = {
                while (true) {
                    delay(interval)
                    time = format.format(
                        System.currentTimeMillis()
                    )
                }
            })

            Text(
                text = time,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF08B906),
                modifier = Modifier
                    .onSizeChanged {
                        width = it.width
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = {
                            isShow = true
                        })
                    },
                fontFamily = FontFamily.Default
            )
            if (isShow) {
                AlertDialog(onDismissRequest = {
                    isShow = false
                },
                    title = {
                        Text(text = "请输入时间更新间隔（毫秒）：")
                    },
                    text = {
                        TextField(
                            value = interval.toString(),
                            onValueChange = {
                                it.toLongOrNull()?.let { intervalLong ->
                                    interval = intervalLong
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent
                            ),
                            placeholder = {
                                Text(text = "请输入时间更新间隔")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            isShow = false
                        }) {
                            Text(text = "确定")
                        }

                    }
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(with(LocalDensity.current) { width.toDp() })
                    .background(Color.White)
                    .height(8.75.dp)
            )
        }
    }

    @Composable
    private fun AntiFraud() {

        Spacer(modifier = Modifier.padding(top = 16.dp))
        val context = LocalContext.current
        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1E41DD)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        ImagePreviewActivity.start(context = context)
                    }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "注册 “金钟罩” 、国家反诈中心",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(top = 2.dp))
                Text(text = "科技防诈让你远离诈骗侵害", color = Color.White, fontSize = 16.sp)
            }

        }
//            Image(
//                painterResource(id = R.drawable.fanzha1),
//                contentDescription = null,
//                contentScale = ContentScale.FillBounds,
//                modifier = Modifier
//                    .padding(
//                        top = 16.dp,
//                    )
//                    .fillMaxWidth()
//                    .height(100.dp)
//                    .clip(RoundedCornerShape(4.dp))
//                    .clickable {
//                        ImagePreviewActivity.start(context = context)
//                    }
//            )
    }

    @Composable
    private fun PassInformation(passTypeName: String) {
        var isShow by rememberSaveable {
            mutableStateOf(false)
        }
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .background(color = Color.White)
                .animateContentSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isShow = !isShow
                        },
                    )
                }
                .padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 15.dp,
//                    end = 20.dp
                )

        ) {

            Column(Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 5.dp, end = 20.dp)
                ) {
                    Box(modifier = Modifier.weight(1f), Alignment.Center) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
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
                                text = "桂电学生$passTypeName",
                                fontSize = 14.sp,
                                color = Color(0xFF323232),
//                        modifier = Modifier.weight(1f)
                            )
                        }

                    }

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
                        ),
                        thickness = 0.75.dp
                    )
                    val info = if (passTypeName == "桂电学生临时通行证（备案制）") {
                        "不限次数\n限制通行2次，当天有效，从首次通行开始，到当天22点或者从首次通行后2小时，取小值。每天 05:00~21:59 内可申请。"
                    } else {
                        "每1天可使用4次，剩余3次\n提交后系统自动审核，总限制通行4次。"
                    }
                    Text(
                        text = info,
                        color = Color(0xFF969799),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(end = 20.dp)
                    )
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
            Box(modifier = Modifier.fillMaxWidth()) {
//                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "桂电畅行证",
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = Color.White,
                    fontSize = 15.sp
                )
                val context = LocalContext.current
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(0.01.dp, Color(133, 133, 133)),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterEnd)
                        .wrapContentWidth()
                        .padding(end = 8.dp),
                    //                                            .height(32.dp),
                    color = Color(0x33000000),
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