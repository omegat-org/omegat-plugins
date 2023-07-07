@file:JvmName("ImportPublicKey")
@file:CompilerOptions("-jvm-target", "11")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import java.net.HttpURLConnection
import java.net.URL
import kotlin.io.path.deleteIfExists

if (args.size != 1) {
    System.err.println("Usage: <command> 'GitHub username' ")
    System.exit(1)
}

val username = args.get(0)
val keyring = "/tmp/trusteddb.gpg"
val publicKeyUrl = "https://api.github.com/users/$username/gpg_keys"

System.out.println("Download public key for GitHub user $username")
val connection = URL(publicKeyUrl).openConnection() as HttpURLConnection
connection.requestMethod = "GET"
val json = connection.inputStream.bufferedReader().use { it.readText() }
if (connection.responseCode != 200) {
    System.err.println("Download failed: ${connection.responseMessage}")
    System.exit(1)
}

// Save the public key to a file
var sb = StringBuilder()
Json.parseToJsonElement(json).jsonArray
        .filterIsInstance<JsonObject>()
        .filter { it.containsKey("raw_key")}
        .map { it["raw_key"]}
        .filter { !it.toString().equals("null")}
        .map { it.toString() }
        .forEach {
            sb.append(it.replace("\\r\\n", "\n").replace("\"", "")) .append("\n")
        }
val publicKeyFilePath = kotlin.io.path.createTempFile("public", "key")
val publicKeyFile = publicKeyFilePath.toFile()
publicKeyFile.writeText(sb.toString())

// clear keyring
kotlin.io.path.Path(keyring).deleteIfExists()

// Import the public key into GnuPG keyring
val importCommand = "gpg --no-default-keyring --keyring $keyring --import ${publicKeyFilePath.toAbsolutePath()} --import-options import-show"
val result = Runtime.getRuntime().exec(importCommand).waitFor()

publicKeyFile.deleteOnExit()
System.out.println("$keyring created")
