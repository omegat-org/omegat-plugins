@file:JvmName("CreateDatabase")
@file:CompilerOptions("-jvm-target", "11")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0", "commons-codec:commons-codec:1.15")

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.jar.JarFile
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream

val baseUrl = "https://media.githubusercontent.com/media/omegat-org/omegat-plugins/main/"
val terms = mapOf("Plugin-Name" to "Name", "Bundle-Name" to "Name", "Implementation-Title" to "Name",
        "Plugin-Version" to "Version", "Bundle-Version" to "Version", "Implementation-Version" to "Version",
        "Plugin-Author" to "Author", "Implementation-Vendor" to "Author", "Built-By" to "Author",
        "Plugin-Date" to "Date",
        "Plugin-Description" to "Description", "Plugin-Link" to "Link", "Plugin-Category" to "Category",
        "Plugin-License" to "License",
        "OmegaT-Plugins" to "Class-Name",
        )
val targetFile = "build/dist/plugins.json"
val sourceDir = Paths.get("plugins").toAbsolutePath()

@Serializable
val plugins = mutableListOf<Map<String, String>>()

fun process(pluginFile: Path) {
    val parentDir: Path = pluginFile.toAbsolutePath().getParent()
    val id = parentDir.toFile().relativeToOrSelf(sourceDir.toFile()).toString().replace('/', '.')
    val attributes = mutableMapOf(
            "ID" to id,
            "Plugin-Jar-Filename" to pluginFile.fileName.toString(),
            "Plugin-Download-Url" to baseUrl + pluginFile.toString(),
            "Plugin-Sha256Sum" to DigestUtils(MessageDigestAlgorithms.SHA_256).digestAsHex(pluginFile.inputStream()),
    )
    val jarAttributes = JarFile(pluginFile.toFile()).manifest.mainAttributes
    jarAttributes.forEach { attr -> terms.get(attr.key.toString())?.let {
        if (it.equals("Class-Name")) {
            attributes.put(it, attr.value.toString().split(" ")[0])
        } else {
            attributes.put(it, attr.value.toString())
        }
    }}
    plugins.add(attributes)
}

val format = Json { prettyPrint = true }
val dir = "plugins"
Paths.get(targetFile).parent.createDirectories()
Files.walk(Paths.get(dir))
        .filter {Files.isRegularFile(it)}
        .filter {it.fileName.toString().endsWith(".jar")}
        .forEach {process(it)}
File(targetFile).writeText(format.encodeToString(plugins))

