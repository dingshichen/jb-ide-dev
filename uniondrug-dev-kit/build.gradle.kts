plugins {
    id("org.jetbrains.intellij") version "1.7.0"
}

version = "1.4"

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
//    version.set("2022.1")
    localPath.set("/Applications/IntelliJ IDEA.app")
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