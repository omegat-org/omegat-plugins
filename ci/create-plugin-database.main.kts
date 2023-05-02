@file:JvmName("CreateDatabase")
@file:CompilerOptions("-jvm-target", "11")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0", "commons-codec:commons-codec:1.15")

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream

val baseUrl = "https://github.com/omegat-org/omegat-plugins/raw/continuous-release/plugins/"
val terms = mapOf("Plugin-Name" to "Name", "Bundle-Name" to "Name", "Implementation-Title" to "Name",
        "Plugin-Version" to "Version", "Bundle-Version" to "Version", "Implementation-Version" to "Version",
        "Plugin-Author" to "Author", "Implementation-Vendor" to "Author", "Built-By" to "Author",
        "Plugin-Date" to "Date",
        "Plugin-Description" to "Description", "Plugin-Link" to "Link", "Plugin-Category" to "Category",
        "Plugin-License" to "License")
val targetFile = "build/dist/plugins.json"

@Serializable
val plugins = mutableListOf<Map<String, String>>()

fun process(pluginFile: Path) {
    val plugin = mutableMapOf<String, String>()
    JarFile(pluginFile.toFile()).manifest.mainAttributes.forEach {
        attr -> terms.get(attr.key.toString())?.let {
            plugin.put(it, attr.value.toString())
        }
    }
    plugin.put("Plugin-Jar-Filename", pluginFile.fileName.toString())
    plugin.put("Plugin-Download-Url", baseUrl + pluginFile.fileName.toString())
    plugin.put("Plugin-Sha256Sum", DigestUtils(MessageDigestAlgorithms.SHA_256).digestAsHex(pluginFile.inputStream()))
    plugins.add(plugin)
}

val dir = "plugins"
Paths.get(targetFile).parent.createDirectories()
Files.walk(Paths.get(dir))
        .filter {Files.isRegularFile(it)}
        .filter {it.fileName.toString().endsWith(".jar")}
        .forEach {process(it)}
File(targetFile).writeText(Json.encodeToString(plugins))

