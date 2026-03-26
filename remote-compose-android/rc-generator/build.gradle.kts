plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("com.example.rcgenerator.MainKt")
}

dependencies {
    implementation(libs.androidx.compose.remote.core)
    implementation(libs.androidx.compose.remote.creation)
    implementation(libs.androidx.compose.remote.creation.core)
    implementation(libs.androidx.annotation)
}

kotlin {
    jvmToolchain(17)
}

// generateRc：產生 .rc 檔案到 rc-generator/output/
tasks.register<JavaExec>("generateRc") {
    group = "remote-compose"
    description = "Generate .rc files from RemoteComposeWriter"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.example.rcgenerator.MainKt")
    args = listOf(layout.buildDirectory.dir("rc-output").get().asFile.absolutePath)
}

// publishRc：把 .rc 複製到 remote-compose-server/static/
tasks.register<Copy>("publishRc") {
    group = "remote-compose"
    description = "Copy generated .rc files to remote-compose-server/static/"
    dependsOn("generateRc")
    from(layout.buildDirectory.dir("rc-output"))
    into(rootProject.projectDir.resolve("../remote-compose-server/static"))
}
