plugins {
    id("org.jetbrains.intellij") version "1.11.0"
}

version = "1.5.2"

dependencies {
    implementation(project(":common"))
    implementation(project(":torna-client"))
    implementation(project(":mock"))
}

intellij {
    pluginName.set("Uniondrug Golang Kit")
    type.set("GO")
    version.set("2022.3")
//    localPath.set("/Users/dingshichen/Library/Application Support/JetBrains/Toolbox/apps/Goland/ch-0/223.7571.176/GoLand.app")
    plugins.set(listOf("markdown", "org.jetbrains.plugins.go"))
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