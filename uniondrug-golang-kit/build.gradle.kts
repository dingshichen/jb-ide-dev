plugins {
    id("org.jetbrains.intellij") version "1.6.0"
}

version = "1.1"

dependencies {
    implementation(project(":common"))
    implementation(project(":torna-client"))
    implementation(project(":mock"))
}

intellij {
    pluginName.set("Uniondrug Golang Kit")
    type.set("GO")
//    version.set("2022.1")
    localPath.set("/Applications/GoLand.app")
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