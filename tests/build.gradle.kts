plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":app"))
    testImplementation(kotlin("test"))

    val jUnitVersion = "5.7.1"
    testImplementation("org.junit.jupiter:junit-jupiter:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")

    val kotestVersion = "5.4.2"
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Testcontainers
    val testcontainersVersion = "1.17.6"
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:2.35.0")

    // Ktor specific
    val ktorVersion = "2.3.1"
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    // DB
    val jetbrainsExposedVersion = "0.41.1"
    implementation("org.jetbrains.exposed:exposed-core:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$jetbrainsExposedVersion")

    // Mock-oauth2-server
    val mockOAuth2ServerVersion = "0.5.9"
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")

}

tasks {
    withType<Test> {
        dependsOn(":app:shadowJar")
    }
}
