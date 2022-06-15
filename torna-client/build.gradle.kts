plugins {
    `java-library`
}

dependencies {
    implementation(project(":common"))
    implementation(project(":mock"))
    testImplementation(kotlin("test-junit"))
}