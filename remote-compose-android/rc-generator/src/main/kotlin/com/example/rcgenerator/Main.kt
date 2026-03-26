package com.example.rcgenerator

import java.io.File

fun main(args: Array<String>) {
    val outputDir = File(args.firstOrNull() ?: "output").also { it.mkdirs() }

    val files = mapOf(
        "home-v1.rc" to buildHomeV1(),
        "home-v2.rc" to buildHomeV2(),
    )

    files.forEach { (name, bytes) ->
        val file = outputDir.resolve(name)
        file.writeBytes(bytes)
        println("Generated: ${file.absolutePath} (${bytes.size} bytes)")
    }
}
