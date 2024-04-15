plugins {
    java
    kotlin("jvm") version "1.9.23" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
