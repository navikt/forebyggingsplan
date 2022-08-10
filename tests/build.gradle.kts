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

    // Testcontainers
    val testcontainersVersion = "1.17.3"
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")

    // Fuel HTTP client
    val fuelVersion = "2.3.1"
    testImplementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    testImplementation("com.github.kittinunf.fuel:fuel-kotlinx-serialization:$fuelVersion")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")

}

tasks {
    withType<Test>{
        dependsOn(":app:shadowJar")
    }
}
