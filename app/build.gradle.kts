val arrowKtVersion = "2.2.2"
val flywayPostgresqlVersion = "12.0.3"
val hikariCPVersion = "7.0.2"
val ktorVersion = "3.4.0"
val jetbrainsExposedVersion = "1.1.1"
val kotlinxDatetimeVersion = "0.7.1-0.6.x-compat"
val logbackVersion = "1.5.32"
val logbackEncoderVersion = "9.0"
val nettyCodecHttpVersion = "4.2.10.Final"
val nimbusJoseJwtVersion = "10.8"
val prometheusVersion = "1.16.3"

plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    id("application")
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-double-receive:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    // JWT utilities
    implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJoseJwtVersion")

    // altinn-rettigheter-proxy bruker codec 1.11 som har en sårbarhet
    implementation("commons-codec:commons-codec:1.21.0")

    // Database
    implementation("org.postgresql:postgresql:42.7.10")
    implementation("com.zaxxer:HikariCP:$hikariCPVersion")
    implementation("org.flywaydb:flyway-database-postgresql:$flywayPostgresqlVersion")
    implementation("org.jetbrains.exposed:exposed-core:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$jetbrainsExposedVersion")
    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")

    // Serialisering av dato-objekter
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    // Funksjonelle operatorer
    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")

    // metrics
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")
    implementation("io.netty:netty-codec-http:$nettyCodecHttpVersion")

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

application {
    mainClass.set("MainKt")
}

tasks {
    withType<Test> {
        dependsOn(installDist)
    }
}

kotlin {
    jvmToolchain(21)
}
