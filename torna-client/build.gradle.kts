plugins {
    `java-library`
}

dependencies {
    implementation(project(":common"))
    implementation(project(":mock"))
    implementation("com.google.code.gson:gson:2.8.6")
    testImplementation(kotlin("test-junit"))
}