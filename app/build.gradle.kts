val ktorVersion = "2.3.12"
val altinnKlientVersion = "4.0.0"
val jetbrainsExposedVersion = "0.55.0"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
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
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    // JWT utilities
    implementation("com.nimbusds:nimbus-jose-jwt:9.41.2")


    implementation("no.nav.arbeidsgiver:altinn-rettigheter-proxy-klient:$altinnKlientVersion")
    // altinn-rettigheter-proxy bruker codec 1.11 som har en sårbarhet
    implementation("commons-codec:commons-codec:1.17.1")

    // Database
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.19.0")
    implementation("org.jetbrains.exposed:exposed-core:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$jetbrainsExposedVersion")
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.8")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")

    // Serialisering av dato-objekter
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Funksjonelle operatorer
    implementation("io.arrow-kt:arrow-core:1.2.4")

    // metrics
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:1.13.5")
    implementation("io.netty:netty-codec-http:4.1.114.Final")

}

application {
    mainClass.set("MainKt")
}

tasks {
    shadowJar {
        mergeServiceFiles()
        manifest {
            attributes(Pair("Main-Class", "MainKt"))
        }
    }

    withType<Test> {
        dependsOn(shadowJar)
    }
}

kotlin {
    jvmToolchain(17)
}
