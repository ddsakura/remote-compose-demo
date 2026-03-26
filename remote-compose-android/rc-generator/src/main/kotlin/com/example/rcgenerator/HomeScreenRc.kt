package com.example.rcgenerator

import androidx.compose.remote.core.operations.Header
import androidx.compose.remote.core.operations.layout.managers.BoxLayout
import androidx.compose.remote.core.operations.layout.managers.ColumnLayout
import androidx.compose.remote.core.operations.layout.managers.CoreText
import androidx.compose.remote.core.operations.layout.modifiers.ShapeType
import androidx.compose.remote.creation.JvmRcPlatformServices
import androidx.compose.remote.creation.RemoteComposeWriter
import androidx.compose.remote.creation.actions.HostAction
import androidx.compose.remote.creation.modifiers.RecordingModifier
import androidx.compose.remote.creation.modifiers.RoundedRectShape

// Action IDs：與 app 端約定一致
const val ACTION_PRIMARY_BUTTON = 1001
const val ACTION_SECONDARY_BUTTON = 1002

private val platform = JvmRcPlatformServices()
private const val DENSITY = 2.625f
private fun dp(value: Int): Float = value * DENSITY
private fun sp(value: Int): Float = value * DENSITY

private fun argb(hex: String): Int {
    val clean = hex.trimStart('#')
    return when (clean.length) {
        6 -> ("FF$clean").toLong(16).toInt()
        8 -> clean.toLong(16).toInt()
        else -> 0xFF000000.toInt()
    }
}

fun buildHomeV1(): ByteArray {
    val writer = RemoteComposeWriter(
        platform,
        RemoteComposeWriter.HTag(Header.DOC_WIDTH, (400 * DENSITY).toInt()),
        RemoteComposeWriter.HTag(Header.DOC_HEIGHT, (600 * DENSITY).toInt()),
    )

    val rootMod = RecordingModifier()
        .fillMaxSize()
        .background(argb("#FFFFFF"))
        .padding(dp(24))

    writer.root {
        writer.column(rootMod, ColumnLayout.START, ColumnLayout.TOP) {
            writer.textComponent(RecordingModifier(), writer.addText("Remote Compose Spike"),
                argb("#1A73E8"), sp(24), 0, 400f, null, CoreText.TEXT_ALIGN_START, 0, Int.MAX_VALUE) {}
            spacer(writer, 8)
            writer.textComponent(RecordingModifier(), writer.addText("這段 UI 完全由 Server 控制"),
                argb("#666666"), sp(14), 0, 400f, null, CoreText.TEXT_ALIGN_START, 0, Int.MAX_VALUE) {}
            spacer(writer, 4)
            writer.textComponent(RecordingModifier(), writer.addText("版本：V1"),
                argb("#999999"), sp(12), 0, 400f, null, CoreText.TEXT_ALIGN_START, 0, Int.MAX_VALUE) {}
            spacer(writer, 32)
            primaryButton(writer, "主要按鈕", argb("#1A73E8"), ACTION_PRIMARY_BUTTON)
            spacer(writer, 12)
            outlineButton(writer, "次要按鈕", argb("#1A73E8"), ACTION_SECONDARY_BUTTON)
        }
    }
    return writer.encodeToByteArray()
}

fun buildHomeV2(): ByteArray {
    val writer = RemoteComposeWriter(
        platform,
        RemoteComposeWriter.HTag(Header.DOC_WIDTH, (400 * DENSITY).toInt()),
        RemoteComposeWriter.HTag(Header.DOC_HEIGHT, (600 * DENSITY).toInt()),
    )

    val rootMod = RecordingModifier()
        .fillMaxSize()
        .background(argb("#FFF0F5"))
        .padding(dp(24))

    writer.root {
        writer.column(rootMod, ColumnLayout.START, ColumnLayout.TOP) {
            writer.textComponent(RecordingModifier(), writer.addText("Remote Compose Spike"),
                argb("#E84393"), sp(24), 0, 400f, null, CoreText.TEXT_ALIGN_START, 0, Int.MAX_VALUE) {}
            spacer(writer, 8)
            writer.textComponent(RecordingModifier(), writer.addText("Server 更新了，App 沒有重裝！"),
                argb("#666666"), sp(14), 0, 400f, null, CoreText.TEXT_ALIGN_START, 0, Int.MAX_VALUE) {}
            spacer(writer, 4)
            writer.textComponent(RecordingModifier(), writer.addText("版本：V2"),
                argb("#999999"), sp(12), 0, 400f, null, CoreText.TEXT_ALIGN_START, 0, Int.MAX_VALUE) {}
            spacer(writer, 32)
            primaryButton(writer, "主要按鈕（V2）", argb("#E84393"), ACTION_PRIMARY_BUTTON)
        }
    }
    return writer.encodeToByteArray()
}

private fun spacer(writer: RemoteComposeWriter, heightDp: Int) {
    writer.startBox(RecordingModifier().height(dp(heightDp)))
    writer.endBox()
}

private fun primaryButton(writer: RemoteComposeWriter, label: String, bgColor: Int, actionId: Int) {
    val radius = dp(8)
    val shape = RoundedRectShape(radius, radius, radius, radius)
    val mod = RecordingModifier()
        .fillMaxWidth()
        .clip(shape)
        .background(bgColor)
        .onClick(HostAction(actionId, writer.addText(label)))
        .padding(dp(16), dp(14), dp(16), dp(14))
    writer.startBox(mod, BoxLayout.CENTER, BoxLayout.CENTER)
    writer.textComponent(RecordingModifier(), writer.addText(label),
        argb("#FFFFFF"), sp(16), 0, 600f, null, CoreText.TEXT_ALIGN_CENTER, 0, Int.MAX_VALUE) {}
    writer.endBox()
}

private fun outlineButton(writer: RemoteComposeWriter, label: String, borderColor: Int, actionId: Int) {
    val radius = dp(8)
    val shape = RoundedRectShape(radius, radius, radius, radius)
    val mod = RecordingModifier()
        .fillMaxWidth()
        .border(dp(1), dp(8), borderColor, ShapeType.ROUNDED_RECTANGLE)
        .clip(shape)
        .background(argb("#FFFFFF"))
        .onClick(HostAction(actionId, writer.addText(label)))
        .padding(dp(16), dp(14), dp(16), dp(14))
    writer.startBox(mod, BoxLayout.CENTER, BoxLayout.CENTER)
    writer.textComponent(RecordingModifier(), writer.addText(label),
        borderColor, sp(16), 0, 600f, null, CoreText.TEXT_ALIGN_CENTER, 0, Int.MAX_VALUE) {}
    writer.endBox()
}
