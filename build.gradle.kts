/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn more about Gradle by exploring our samples at https://docs.gradle.org/7.4.2/samples
 */
plugins {
    java
}
group = "cn.uniondrug.dev"

subprojects {
    apply {
        plugin("java")
    }

    repositories {
        mavenLocal()
        maven { url = uri("https://maven.turboradio.cn/repository/ud-release/") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        mavenCentral()
    }

    val lombok = "1.18.20"

    dependencies {
        compileOnly("org.projectlombok:lombok:$lombok")
        annotationProcessor("org.projectlombok:lombok:$lombok")
        testCompileOnly("org.projectlombok:lombok:$lombok")
        testAnnotationProcessor("org.projectlombok:lombok:$lombok")
    }

    tasks {
        compileJava {
            options.release.set(11)
            options.compilerArgs.add("-parameters")
        }
        compileTestJava {
            options.release.set(11)
            options.compilerArgs.add("-parameters")
        }
    }
}