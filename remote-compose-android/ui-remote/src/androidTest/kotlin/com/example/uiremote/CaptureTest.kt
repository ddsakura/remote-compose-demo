package com.example.uiremote

import androidx.compose.remote.creation.compose.captureSingleRemoteDocument
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * 在裝置上執行，將 Composable 捕捉為 .rc binary，再用 adb pull 拉回電腦。
 *
 * 執行：
 *   ./gradlew :ui-remote:connectedAndroidTest
 *
 * 拉回 .rc 到 server static 目錄：
 *   adb pull /sdcard/home-v1.rc ../remote-compose-server/static/home-v1.rc
 *   adb pull /sdcard/home-v2.rc ../remote-compose-server/static/home-v2.rc
 */
@RunWith(AndroidJUnit4::class)
class CaptureTest {

    @Test
    fun captureHomeV1() {
        val bytes: ByteArray = captureSingleRemoteDocument {
            HomeScreenV1()
        }
        File("/sdcard/home-v1.rc").writeBytes(bytes)
    }

    @Test
    fun captureHomeV2() {
        val bytes: ByteArray = captureSingleRemoteDocument {
            HomeScreenV2()
        }
        File("/sdcard/home-v2.rc").writeBytes(bytes)
    }
}
