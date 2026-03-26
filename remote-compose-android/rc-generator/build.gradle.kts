plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("com.example.rcgenerator.MainKt")
}

dependencies {
    implementation("androidx.compose.remote:remote-core:1.0.0-alpha07")
    implementation("androidx.compose.remote:remote-creation:1.0.0-alpha07")
    implementation("androidx.compose.remote:remote-creation-core:1.0.0-alpha07")
    implementation("androidx.annotation:annotation:1.9.1")
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
