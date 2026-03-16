val flywayPostgresqlVersion = "12.0.3"
val hikariCPVersion = "7.0.2"
val jetbrainsExposedVersion = "1.1.1"
val kotestVersion = "6.1.5"
val kotlinxDatetimeVersion = "0.7.1-0.6.x-compat"
val ktorVersion = "3.4.0"
val mockOAuth2ServerVersion = "3.0.1"
val mockServerVersion = "2.0.3"
val nettyCodecHttpVersion = "4.2.9.Final"
val testcontainersVersion = "2.0.3"
val wiremockVersion = "3.13.2"

plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    application
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":app"))
    testImplementation(kotlin("test"))

    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")

    // Testcontainers
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:testcontainers-kafka:$testcontainersVersion")
    testImplementation("software.xdev.mockserver:testcontainers:${mockServerVersion}")
    testImplementation("software.xdev.mockserver:client:${mockServerVersion}")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:testcontainers-postgresql:$testcontainersVersion")
    testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")

    // Ktor specific
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    // Database
    implementation("org.postgresql:postgresql:42.7.10")
    implementation("com.zaxxer:HikariCP:$hikariCPVersion")
    implementation("org.flywaydb:flyway-database-postgresql:$flywayPostgresqlVersion")
    implementation("org.jetbrains.exposed:exposed-core:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$jetbrainsExposedVersion")

    // Mock-oauth2-server
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")

    constraints {
        implementation("com.fasterxml.jackson.core:jackson-core") {
            version { require("2.21.1") }
            because("versjoner < 2.21.1 har sårbarhet. inkludert i ktor-server-auth:3.4.0")
        }
        implementation("tools.jackson.core:jackson-core") {
            version { require("3.1.0") }
            because("versjoner < 3.1.0 har sårbarhet. inkludert i logstash-logback-encoder:9.0")
        }
    }
}

tasks {
    withType<Test> {
        dependsOn(":app:installDist")
    }
}

kotlin {
    jvmToolchain(21)
}
