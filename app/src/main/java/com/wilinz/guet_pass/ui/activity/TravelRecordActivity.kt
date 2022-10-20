package com.wilinz.guet_pass.ui.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.RowScopeInstance.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wilinz.guet_pass.ui.theme.GuetpassTheme
import com.wilinz.guet_pass.ui.widget.TopRightButton
import com.wilinz.guet_pass.R
import com.wilinz.guet_pass.tools.generateItem
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class TravelRecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuetpassTheme {
                Surface(color = MaterialTheme.colors.surface) {
                    SetSystemUi()
                }
            }
            Scaffold(topBar = {
                val context = LocalContext.current
                TopAppBar(
                    backgroundColor = Color(0xFF08BA07),
                    elevation = 0.dp
                ) {
                    IconButton(onClick = { if (context is Activity) context.finish() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_ios),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(text = "通行记录", color = Color.White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    TopRightButton()
                }
            }) {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {

                    val itemList = remember {
                        generateItem()
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        item {
                            var keyword by remember {
                                mutableStateOf("")
                            }
                            SearchBox(
                                value = keyword,
                                onValueChange = { keyword = it },
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                placeholder = { Text(text = "请输入搜索关键词", color = Color(0xFF969799)) }
                            )
                        }
                        itemsIndexed(itemList) { index, item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 12.dp)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = item.name, fontSize = 17.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(border = BorderStroke(1.dp, Color(0xFF82DFB0))) {
                                        Box(
                                            modifier = Modifier
                                                .wrapContentSize()
                                                .padding(2.dp)
                                        ) {
                                            Text(
                                                text = "允许通行",
                                                fontSize = 12.sp,
                                                color = Color(0xFF06C05F)
                                            )

                                        }
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(text = item.time, color = Color(0xFF666666))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                ) {
                                    item.tags.forEach {
                                        Surface(
                                            border = BorderStroke(0.4.dp, Color(0xFF949899)),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .wrapContentSize()
                                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                                            ) {
                                                Text(
                                                    text = it,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF949899)
                                                )

                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                            }
                            if (index != itemList.lastIndex) {
                                Divider(thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

        }
    }
}

data class Item(val name: String, val tags: List<String>, val time: String)

@Composable
fun SearchBox(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable() () -> Unit = {},
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Surface(
        color = Color(0xFFF7F8FA),
        modifier = modifier
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            decorationBox = { InnerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = null,
                        tint = Color(0xFF323232)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box {
                        InnerTextField()
                        if (value.isEmpty()) {
                            placeholder()
                        }
                    }
                }
            },
            cursorBrush = SolidColor(Color(0xFF58616E)),
            modifier = Modifier.padding(8.dp),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
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
        systemUiController.setStatusBarColor(
            color = Color(0xFF08BA07),
            darkIcons = false
        )

        // setStatusBarColor() and setNavigationBarColor() also exist

        onDispose {}
    }

}
