plugins {
    `java-library`
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib")
    api("com.alibaba:fastjson:1.2.80")
    api("cn.hutool:hutool-all:5.8.0")
    api("com.google.code.gson:gson:2.8.6")
    testImplementation(kotlin("test-junit"))
}