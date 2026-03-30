package com.example.uiremote

import android.content.ContentUris
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.remote.creation.compose.capture.captureSingleRemoteDocument
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking

/**
 * 在裝置上執行，將高階 Remote Compose 範例捕捉為 .rc binary，再用 adb pull 拉回電腦。
 *
 * 執行：
 *   ./gradlew :ui-remote:captureRc
 *
 * 拉回 .rc 到 server static 目錄：
 *   adb pull /sdcard/Download/remote-compose/home-v1.rc ../remote-compose-server/static/home-v1.rc
 *   adb pull /sdcard/Download/remote-compose/home-v2.rc ../remote-compose-server/static/home-v2.rc
 */
@RunWith(AndroidJUnit4::class)
class CaptureTest {

    private val context
        get() = InstrumentationRegistry.getInstrumentation().context

    private fun writeToDownloads(name: String, bytes: ByteArray) {
        val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/remote-compose"
        val resolver = context.contentResolver
        val externalUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI

        val existingUri =
            resolver.query(
                externalUri,
                arrayOf(MediaStore.MediaColumns._ID),
                "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND (${MediaStore.MediaColumns.RELATIVE_PATH} = ? OR ${MediaStore.MediaColumns.RELATIVE_PATH} = ?)",
                arrayOf(name, relativePath, "$relativePath/"),
                null,
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    ContentUris.withAppendedId(externalUri, id)
                } else {
                    null
                }
            }

        val uri = existingUri
            ?: requireNotNull(
                resolver.insert(
                    externalUri,
                    ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                    },
                )
            ) {
                "Failed to create MediaStore entry for $name"
            }

        resolver.openOutputStream(uri, "wt")?.use { output ->
            output.write(bytes)
        } ?: error("Failed to open MediaStore output stream for $name")
    }

    @Test
    fun captureHomeV1() {
        val document = runBlocking {
            captureSingleRemoteDocument(context) {
                RemoteHomeScreenV1()
            }
        }
        writeToDownloads("home-v1.rc", document.bytes)
    }

    @Test
    fun captureHomeV2() {
        val document = runBlocking {
            captureSingleRemoteDocument(context) {
                RemoteHomeScreenV2()
            }
        }
        writeToDownloads("home-v2.rc", document.bytes)
    }
}
