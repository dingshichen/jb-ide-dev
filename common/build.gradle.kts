plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

version = "0.4"

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib")
    api("com.alibaba:fastjson:2.0.3")
    api("cn.hutool:hutool-all:5.7.12")
}