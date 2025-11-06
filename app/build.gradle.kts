val arrowKtVersion = "2.2.0"
val flywayPostgresqlVersion = "11.15.0"
val hikariCPVersion = "7.0.2"
val ktorVersion = "3.3.2"
val jetbrainsExposedVersion = "0.61.0"
val kotlinxDatetimeVersion = "0.7.1-0.6.x-compat"
val logbackVersion = "1.5.20"
val logbackEncoderVersion = "9.0"
val nettyCodecHttpVersion = "4.2.7.Final"
val nimbusJoseJwtVersion = "10.5"
val prometheusVersion = "1.15.5"

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
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
    implementation("commons-codec:commons-codec:1.20.0")

    // Database
    implementation("org.postgresql:postgresql:42.7.8")
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
