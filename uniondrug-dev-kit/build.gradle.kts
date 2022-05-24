plugins {
    id("org.jetbrains.intellij") version "1.6.0"
}

version = "0.4"

dependencies {
    implementation(project(":common"))
    implementation(project(":torna-client"))
    implementation(project(":mock"))
}

intellij {
    pluginName.set("Uniondrug Dev Kit")
    type.set("IU")
//    version.set("2022.1")
    localPath.set("/Applications/IntelliJ IDEA.app")
    plugins.set(listOf("com.intellij.java", "markdown"))
    updateSinceUntilBuild.set(false)
    sameSinceUntilBuild.set(false)
}

tasks {
    patchPluginXml {
        changeNotes.set(file("changeNotes.html").readText())
    }
}