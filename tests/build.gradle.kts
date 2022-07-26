import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app"))
    testImplementation(kotlin("test"))

    val kotestVerstion = "5.3.2"
    testImplementation("io.kotest:kotest-assertions-core:$kotestVerstion")

    // Funksjonelle operatorer
    implementation("io.arrow-kt:arrow-core:1.1.2")
}
