package com.wilinz.guet_pass.ui.widget

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wilinz.guet_pass.R

@Composable
fun TopRightButton(modifier: Modifier=Modifier){
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.01.dp, Color(133, 133, 133)),
        modifier = modifier
            .padding(8.dp)
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
                        context.startActivity(intent);
                    }
                    .padding(vertical = 5.dp), Alignment.Center
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
                        .background(Color(229, 229, 229,0x33))
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