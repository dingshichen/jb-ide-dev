plugins {
    id("org.jetbrains.intellij") version "1.11.0"
}

version = "1.7.4"

dependencies {
    implementation(project(":common"))
    implementation(project(":torna-client"))
    implementation(project(":mss-client"))
    implementation(project(":mock"))
    testImplementation(kotlin("test-junit"))
}

intellij {
    pluginName.set("Uniondrug Dev Kit")
//    type.set("IU")
//    version.set("2022.3")
    localPath.set("/Users/dingshichen/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-0/223.8617.56/IntelliJ IDEA.app/Contents")
    plugins.set(listOf("com.intellij.java", "markdown"))
    updateSinceUntilBuild.set(false)
    sameSinceUntilBuild.set(false)
}

tasks {
    buildSearchableOptions {
        enabled = false
    }
    patchPluginXml {
        changeNotes.set(file("changeNotes.html").readText())
    }
}