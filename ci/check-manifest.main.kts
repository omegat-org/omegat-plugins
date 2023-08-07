@file:JvmName("CheckManifest")
@file:CompilerOptions("-jvm-target", "11")

import java.util.jar.Attributes
import java.util.jar.JarFile

// Mapping from Jar attributes to plugin attribute keys
val terms = mapOf("Plugin-Name" to "Name", "Bundle-Name" to "Name", "Implementation-Title" to "Name",
        "Plugin-Version" to "Version", "Bundle-Version" to "Version", "Implementation-Version" to "Version",
        "Plugin-Author" to "Author", "Implementation-Vendor" to "Author", "Built-By" to "Author",
        "Plugin-Category" to "Category",
        "Plugin-Description" to "Description", "Plugin-Link" to "Link", "OmegaT-Plugins" to "Class-Name",
)

// These are mandatory attributes
val checkList = mutableMapOf("Name" to false, "Version" to false, "Author" to false, "Category" to false,
        "Class-Name" to false)

// Check acceptable category values
fun getAttr(attr: Attributes, term: String): String {
    for (entry in attr) {
        when (terms.get(entry.key.toString())) {
            term -> return entry.value.toString()
        }
    }
    return "unknown"
}

fun getCategory(attr: Attributes): String {
    return getAttr(attr, "Category")
}

val categoryList = mutableListOf("filter", "tokenizer", "marker", "machinetranslator", "base", "glossary", "dictionary",
        "theme", "miscellaneous")

// Accept only one argument as jar file
if (args.size != 1) {
    System.err.println("Multiple jar files seems to be added...")
    System.err.println("It should be exactly one plugin jar file to add.")
    System.exit(1)
}

var targetJarFile = args[0]
val input = JarFile(targetJarFile)
val mainAttr = input.manifest.mainAttributes
for (entry in mainAttr) {
    terms.get(entry.key.toString())?.let { checkList.put(it, true) }
}
val result = checkList.entries.filterNot({it.value}).map({it.key}).firstOrNull()
if (result != null) {
    System.err.println(String.format("%s is missing in MANIFEST.MF", result))
    System.exit(1)
}

val category = getCategory(mainAttr)
if (category !in categoryList) {
    System.err.println(String.format("Category %s is invalid value in MANIFEST.MF", category))
    System.exit(1)
}

// print manifest summary
System.out.println("Manifest summary")
System.out.println(String.format("Name: %s", getAttr(mainAttr, "Name")))
System.out.println(String.format("Version: %s", getAttr(mainAttr, "Version")))
System.out.println(String.format("Author: %s", getAttr(mainAttr, "Author")))
System.out.println(String.format("Category: %s", category))
