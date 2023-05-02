@file:JvmName("CheckManifest")
@file:CompilerOptions("-jvm-target", "11")

import java.util.jar.JarFile

val terms = mapOf("Plugin-Name" to "Name", "Bundle-Name" to "Name", "Implementation-Title" to "Name",
        "Plugin-Version" to "Version", "Bundle-Version" to "Version", "Implementation-Version" to "Version",
        "Plugin-Author" to "Author", "Implementation-Vendor" to "Author", "Built-By" to "Author",
        "Plugin-Description" to "Description", "Plugin-Link" to "Link", "Plugin-Category" to "Category")
val checkList = mutableMapOf("Name" to false, "Version" to false, "Author" to false,
        "Description" to false, "Link" to false, "Category" to false)

var targetJarFile = args.toList().first()
val input = JarFile(targetJarFile)
for (entry in input.manifest.entries) {
    terms.get(entry.key)?.let { checkList.put(it, true) }
}
val result = checkList.entries.filterNot({it.value}).map({it.key}).firstOrNull()
if (result != null) {
    System.err.println(String.format("{} is missing in MANIFEST.MF", result))
    System.exit(1)
}
