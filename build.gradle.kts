/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn more about Gradle by exploring our samples at https://docs.gradle.org/7.4.2/samples
 */
plugins {
    java
    kotlin("jvm") version "1.7.10"
}
group = "cn.uniondrug.dev"

allprojects {

    val lombok = "1.18.20"
    val jdk = JavaVersion.VERSION_11.toString()

    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
    }

    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        mavenCentral()
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:$lombok")
        annotationProcessor("org.projectlombok:lombok:$lombok")
        testCompileOnly("org.projectlombok:lombok:$lombok")
        testAnnotationProcessor("org.projectlombok:lombok:$lombok")
    }

    tasks {
        compileJava {
            options.release.set(jdk.toInt())
            options.compilerArgs.add("-parameters")
        }
        compileTestJava {
            options.release.set(jdk.toInt())
            options.compilerArgs.add("-parameters")
        }
        compileKotlin {
            kotlinOptions.jvmTarget = jdk
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = jdk
        }
    }
}