import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.LinkedHashMap

buildscript {
    repositories { mavenCentral() }
    dependencies { classpath("org.yaml:snakeyaml:2.2") }
}

plugins {
    `java-library`
    kotlin("jvm") version "2.3.20"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "com.github.darksoulq"
val mcVersion = stonecutter.current.project
version = "2.4.0-mc.${mcVersion}-alpha.1"

val yamlParser = Yaml()

fun parseYaml(f: File): Map<String, Any> {
    if (!f.exists()) return emptyMap()
    val obj = yamlParser.load<Any>(f.readText())
    @Suppress("UNCHECKED_CAST")
    return (obj as? Map<String, Any>) ?: emptyMap()
}

fun readRemovals(f: File): List<String> {
    if (!f.exists()) return emptyList()
    return f.readLines().map { it.trim() }.filter { it.isNotEmpty() }
}

fun mergeMaps(global: Map<String, Any>, specific: Map<String, Any>, removals: List<String>): Map<String, Any> {
    val result = LinkedHashMap(global)
    specific.forEach { (k, v) ->
        if (v.toString().trim() == "remove") result.remove(k)
        else result[k] = v
    }
    removals.forEach { result.remove(it) }
    return result
}

val versionsMap = parseYaml(File(rootDir, "gradle/versions.yml"))
val activeConfig = (versionsMap[mcVersion] as? Map<*, *>)
    ?: (versionsMap["26.1.2"] as? Map<*, *>)
    ?: mapOf("java" to 25, "paperweight" to "26.1.2.build.+", "apiVersion" to "26.1.2")

val targetJavaVersion = activeConfig["java"]?.toString()?.toIntOrNull() ?: 25
val paperweightStr = activeConfig["paperweight"]?.toString()?.replace("{version}", mcVersion) ?: ""
val apiVersionStr = activeConfig["apiVersion"]?.toString()?.replace("{version}", mcVersion) ?: ""

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    withSourcesJar()
    withJavadocJar()
}

kotlin { jvmToolchain(targetJavaVersion) }

val activeRepos = mergeMaps(
    parseYaml(File(rootDir, "gradle/repos/global.yml")),
    parseYaml(File(rootDir, "gradle/repos/${mcVersion}/repos.yml")),
    readRemovals(File(rootDir, "gradle/repos/${mcVersion}/removal.txt"))
)

repositories {
    activeRepos.forEach { (name, url) ->
        if (url == "default") { if (name == "mavenCentral") mavenCentral() }
        else { maven(url.toString()) }
    }
}

val activeLibs = mergeMaps(
    parseYaml(File(rootDir, "gradle/libs/global.yml")),
    parseYaml(File(rootDir, "gradle/libs/${mcVersion}/libs.yml")),
    readRemovals(File(rootDir, "gradle/libs/${mcVersion}/removal.txt"))
)

dependencies {
    paperweight.paperDevBundle(paperweightStr)

    activeLibs.forEach { (_, notation) ->
        when (notation) {
            is Map<*, *> -> {
                val type = (notation["type"] as? String) ?: "compileOnly"
                val group = notation["group"]
                val name = notation["name"] ?: notation["module"]
                val ver = notation["version"]
                val dep = if (group != null && name != null && ver != null) "$group:$name:$ver" else notation["module"].toString()
                add(type, dep)
            }
            is String -> add("compileOnly", notation)
        }
    }
}

runPaper { folia.registerTask() }

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(targetJavaVersion)
    }

    named<xyz.jpenilla.runpaper.task.RunServer>("runServer") {
        minecraftVersion(mcVersion)
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    named<xyz.jpenilla.runpaper.task.RunServer>("runFolia") {
        minecraftVersion(mcVersion)
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf("version" to project.version.toString(), "apiVersion" to apiVersionStr)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") { expand(props) }
    }

    val excludesFile = File(rootDir, "gradle/javadoc-excludes.txt")
    val javadocExcludesList = if (excludesFile.exists()) {
        excludesFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }
    } else {
        emptyList()
    }

    javadoc {
        val standardOptions = options as StandardJavadocDocletOptions
        standardOptions.apply {
            encoding = "UTF-8"
            charSet = "UTF-8"
            memberLevel = JavadocMemberLevel.PUBLIC
            isAuthor = true
            isVersion = true
            links("https://docs.oracle.com/en/java/javase/$targetJavaVersion/docs/api/")
        }
        exclude(javadocExcludesList)
    }
}