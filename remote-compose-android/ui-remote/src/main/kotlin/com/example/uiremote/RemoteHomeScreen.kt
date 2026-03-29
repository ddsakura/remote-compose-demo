package com.example.uiremote

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.action.HostAction
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteColumn
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteSpacer
import androidx.compose.remote.creation.compose.layout.RemoteText
import androidx.compose.remote.creation.compose.modifier.background
import androidx.compose.remote.creation.compose.modifier.border
import androidx.compose.remote.creation.compose.modifier.clickable
import androidx.compose.remote.creation.compose.modifier.clip
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.modifier.fillMaxWidth
import androidx.compose.remote.creation.compose.modifier.height
import androidx.compose.remote.creation.compose.modifier.padding
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.shapes.RemoteRoundedCornerShape
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rdp
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.remote.creation.compose.state.rsp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

private val ButtonShape = RemoteRoundedCornerShape(8.rdp)

@Composable
@RemoteComposable
fun RemoteHomeScreenV1() {
    RemoteHomeScreen(
        backgroundColor = Color.White,
        titleColor = Color(0xFF1A73E8),
        subtitle = "這段 UI 完全由 Server 控制",
        versionLabel = "版本：RemoteHomeScreenV1",
        primaryLabel = "主要按鈕",
        primaryColor = Color(0xFF1A73E8),
        secondaryLabel = "次要按鈕",
    )
}

@Composable
@RemoteComposable
fun RemoteHomeScreenV2() {
    RemoteHomeScreen(
        backgroundColor = Color(0xFFFFF0F5),
        titleColor = Color(0xFFE84393),
        subtitle = "Server 更新了，App 沒有重裝！",
        versionLabel = "版本：RemoteHomeScreenV2",
        primaryLabel = "主要按鈕（V2）",
        primaryColor = Color(0xFFE84393),
        secondaryLabel = null,
    )
}

@Composable
@RemoteComposable
private fun RemoteHomeScreen(
    backgroundColor: Color,
    titleColor: Color,
    subtitle: String,
    versionLabel: String,
    primaryLabel: String,
    primaryColor: Color,
    secondaryLabel: String?,
) {
    RemoteColumn(
        modifier = RemoteModifier.fillMaxSize().background(backgroundColor).padding(24.rdp),
    ) {
        RemoteText(
            text = "Remote Compose Spike",
            color = titleColor.rc,
            fontSize = 24.rsp,
            fontWeight = FontWeight.SemiBold,
        )
        RemoteSpacer(modifier = RemoteModifier.height(8.rdp))
        RemoteText(
            text = subtitle,
            color = Color(0xFF666666).rc,
            fontSize = 14.rsp,
        )
        RemoteSpacer(modifier = RemoteModifier.height(4.rdp))
        RemoteText(
            text = versionLabel,
            color = Color(0xFF999999).rc,
            fontSize = 12.rsp,
        )
        RemoteSpacer(modifier = RemoteModifier.height(32.rdp))
        RemotePrimaryButton(
            label = primaryLabel,
            backgroundColor = primaryColor,
            actionId = ACTION_PRIMARY_BUTTON,
        )
        if (secondaryLabel != null) {
            RemoteSpacer(modifier = RemoteModifier.height(12.rdp))
            RemoteOutlineButton(
                label = secondaryLabel,
                borderColor = primaryColor,
                actionId = ACTION_SECONDARY_BUTTON,
            )
        }
    }
}

@Composable
@RemoteComposable
private fun RemotePrimaryButton(label: String, backgroundColor: Color, actionId: Int) {
    RemoteBox(
        modifier =
            RemoteModifier
                .fillMaxWidth()
                .clip(ButtonShape)
                .background(backgroundColor)
                .clickable(namedHostAction(actionId, label))
                .padding(left = 16.rdp, top = 14.rdp, right = 16.rdp, bottom = 14.rdp),
        contentAlignment = RemoteAlignment.Center,
    ) {
        RemoteText(
            text = label,
            color = Color.White.rc,
            fontSize = 16.rsp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@RemoteComposable
private fun RemoteOutlineButton(label: String, borderColor: Color, actionId: Int) {
    RemoteBox(
        modifier =
            RemoteModifier
                .fillMaxWidth()
                .border(width = 1.rdp, color = borderColor.rc, shape = ButtonShape)
                .clip(ButtonShape)
                .background(Color.White)
                .clickable(namedHostAction(actionId, label))
                .padding(left = 16.rdp, top = 14.rdp, right = 16.rdp, bottom = 14.rdp),
        contentAlignment = RemoteAlignment.Center,
    ) {
        RemoteText(
            text = label,
            color = borderColor.rc,
            fontSize = 16.rsp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

// AndroidX currently exposes HostAction for compose-style authoring as a restricted alpha API.
// Keep the usage isolated here so the rest of the sample stays on public-facing code where possible.
@SuppressLint("RestrictedApi")
private fun namedHostAction(actionId: Int, label: String): HostAction =
    HostAction(
        name = label.rs,
        type = HostAction.Type.NONE,
        id = actionId,
    )

@Preview(showBackground = true)
@Composable
private fun RemoteHomeScreenV1Preview() {
    RemoteHomeScreenV1()
}

@Preview(showBackground = true)
@Composable
private fun RemoteHomeScreenV2Preview() {
    RemoteHomeScreenV2()
}
