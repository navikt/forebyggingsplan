val kotestVersion = "5.9.1"
val testcontainersVersion = "1.20.6"
val ktorVersion = "3.1.1"
val jetbrainsExposedVersion = "0.60.0"
val mockOAuth2ServerVersion = "2.1.10"

val testMockServerVersion = "5.15.0"

plugins {
    kotlin("jvm")
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
    testImplementation("org.testcontainers:kafka:$testcontainersVersion")
    testImplementation("org.testcontainers:mockserver:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("org.wiremock:wiremock-standalone:3.12.1")

    // Ktor specific
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Database
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.flywaydb:flyway-database-postgresql:11.3.4")
    implementation("org.jetbrains.exposed:exposed-core:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$jetbrainsExposedVersion")

    // Mock-oauth2-server
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")

    testImplementation("org.mock-server:mockserver-client-java:$testMockServerVersion")

    constraints {
        implementation("net.minidev:json-smart") {
            version {
                require("2.5.2")
            }
            because(
                "From Kotlin version: 1.7.20 -> Earlier versions of json-smart package are vulnerable to Denial of Service (DoS) due to a StackOverflowError when parsing a deeply nested JSON array or object.",
            )
        }
        implementation("io.netty:netty-codec-http2") {
            version {
                require("4.1.119.Final")
            }
            because("From Ktor version: 2.3.5 -> io.netty:netty-codec-http2 vulnerable to HTTP/2 Rapid Reset Attack")
        }
        testImplementation("com.google.guava:guava") {
            version {
                require("33.4.0-jre")
            }
            because("Mockserver har sårbar guava versjon")
        }
        testImplementation("org.bouncycastle:bcprov-jdk18on") {
            version {
                require("1.80")
            }
            because("bcprov-jdk18on in Mockserver har sårbar versjon")
        }
        testImplementation("org.bouncycastle:bcpkix-jdk18on") {
            version {
                require("1.80")
            }
            because("bcpkix-jdk18on in Mockserver har sårbar versjon")
        }
        testImplementation("org.xmlunit:xmlunit-core") {
            version {
                require("2.10.0")
            }
            because("xmlunit-core in Mockserver har sårbar versjon")
        }
        testImplementation("org.apache.commons:commons-compress") {
            version {
                require("1.27.1")
            }
            because("testcontainers har sårbar versjon")
        }
        testImplementation("com.jayway.jsonpath:json-path") {
            version {
                require("2.9.0")
            }
            because(
                """
                json-path v2.8.0 was discovered to contain a stack overflow via the Criteria.parse() method.
                introdusert gjennom io.kotest:kotest-assertions-json:5.8.0
                """.trimIndent(),
            )
        }
    }
}

tasks {
    withType<Test> {
        dependsOn(":app:shadowJar")
    }
}

kotlin {
    jvmToolchain(17)
}
