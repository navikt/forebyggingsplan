plugins {
    java
    kotlin("jvm") version "1.7.10" apply false
    kotlin("plugin.serialization") version "1.7.10" apply false
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
