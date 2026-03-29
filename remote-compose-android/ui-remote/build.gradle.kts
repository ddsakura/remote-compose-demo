import java.io.File
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

fun adbExecutable(project: Project): String {
    val adbPath = project.findProperty("adbPath")?.toString()
    if (!adbPath.isNullOrBlank()) return adbPath

    val sdkRoot =
        project.findProperty("android.sdk.path")?.toString()
            ?: System.getenv("ANDROID_SDK_ROOT")
            ?: System.getenv("ANDROID_HOME")

    if (!sdkRoot.isNullOrBlank()) {
        val platformTools = File(sdkRoot, "platform-tools/adb")
        if (platformTools.exists()) return platformTools.absolutePath
    }

    return "adb"
}

fun adbArgs(project: Project, vararg tail: String): List<String> {
    val serial = project.findProperty("androidSerial")?.toString() ?: System.getenv("ANDROID_SERIAL")
    val args = mutableListOf(adbExecutable(project))
    if (!serial.isNullOrBlank()) {
        args += listOf("-s", serial)
    }
    args += tail
    return args
}

fun runCommand(command: List<String>, workingDir: File): Pair<Int, String> {
    val process =
        ProcessBuilder(command)
            .directory(workingDir)
            .redirectErrorStream(true)
            .start()

    val output = process.inputStream.bufferedReader().readText()
    val exitCode = process.waitFor()
    return exitCode to output
}

val capturedRcFiles =
    listOf(
        "home-v1.rc",
        "home-v2.rc",
    )

val deviceCaptureDir = "/sdcard/Download/remote-compose"
val publishedRcDir = rootProject.projectDir.resolve("../remote-compose-server/static")

android {
    namespace = "com.example.uiremote"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Remote Compose Creation
    implementation(libs.androidx.compose.remote.core)
    implementation(libs.androidx.compose.remote.creation)
    implementation(libs.androidx.compose.remote.creation.core)
    implementation(libs.androidx.compose.remote.creation.compose)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)
}

tasks.register("captureRc") {
    group = "remote-compose"
    description = "Run the high-level Remote Compose capture tests on a connected device/emulator."
    dependsOn("connectedDebugAndroidTest")
}

capturedRcFiles.forEach { fileName ->
    val taskName =
        "pull${fileName.substringBefore('.').split('-').joinToString("") { part -> part.replaceFirstChar(Char::uppercase) }}Rc"

    tasks.register(taskName) {
        group = "remote-compose"
        description = "Pull $fileName from the instrumentation external files dir into remote-compose-server/static/."
        dependsOn("captureRc")
        doLast {
            publishedRcDir.mkdirs()

            val fileStem = fileName.removeSuffix(".rc")
            val listCommand = adbArgs(project, "shell", "ls", "-t", deviceCaptureDir)
            val (listExit, listOutput) = runCommand(listCommand, project.projectDir)
            if (listExit != 0) {
                error("Failed to list remote capture files.\n$listOutput")
            }

            val latestFileName =
                listOutput
                    .lineSequence()
                    .map { it.trim() }
                    .filter { it.startsWith(fileStem) && it.endsWith(".rc") }
                    .firstOrNull()
                    ?: error(
                        "No captured file matching $fileStem*.rc was found in $deviceCaptureDir.\n$listOutput"
                    )

            val remotePath = "$deviceCaptureDir/$latestFileName"
            val outputPath = publishedRcDir.resolve(fileName).absolutePath
            val pullCommand = adbArgs(project, "pull", remotePath, outputPath)
            val (pullExit, pullOutput) = runCommand(pullCommand, project.projectDir)
            if (pullExit != 0) {
                error("Failed to pull $remotePath.\n$pullOutput")
            }
            println(pullOutput.trim())
        }
    }
}

tasks.register("publishCapturedRc") {
    group = "remote-compose"
    description = "Capture high-level Remote Compose .rc files on device/emulator and publish them to remote-compose-server/static/."
    dependsOn(
        "pullHomeV1Rc",
        "pullHomeV2Rc",
    )

    doFirst {
        publishedRcDir.mkdirs()
    }
}
