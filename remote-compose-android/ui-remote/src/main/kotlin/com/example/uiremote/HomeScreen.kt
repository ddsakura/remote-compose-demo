package com.example.uiremote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Action IDs：與 App 端共同約定
const val ACTION_PRIMARY_BUTTON = 1001
const val ACTION_SECONDARY_BUTTON = 1002

@Composable
fun HomeScreenV1() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Text("Remote Compose Spike", fontSize = 24.sp, color = Color(0xFF1A73E8))
        Spacer(modifier = Modifier.height(8.dp))
        Text("這段 UI 完全由 Server 控制", fontSize = 14.sp, color = Color(0xFF666666))
        Spacer(modifier = Modifier.height(4.dp))
        Text("版本：V1", fontSize = 12.sp, color = Color(0xFF999999))
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("主要按鈕")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("次要按鈕")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenV1Preview() {
    HomeScreenV1()
}

@Composable
fun HomeScreenV2() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
            .padding(24.dp)
    ) {
        Text("Remote Compose Spike", fontSize = 24.sp, color = Color(0xFFE84393))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Server 更新了，App 沒有重裝！", fontSize = 14.sp, color = Color(0xFF666666))
        Spacer(modifier = Modifier.height(4.dp))
        Text("版本：V2", fontSize = 12.sp, color = Color(0xFF999999))
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("主要按鈕（V2）")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenV2Preview() {
    HomeScreenV2()
}
