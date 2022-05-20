plugins {
    `java-library`
}

version = "0.4"

dependencies {
    implementation(project(":common"))
    implementation(project(":mock"))
    testImplementation(kotlin("test-junit"))
}