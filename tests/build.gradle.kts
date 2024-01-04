plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":app"))
    testImplementation(kotlin("test"))


    val kotestVersion = "5.8.0"
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")

    // Testcontainers
    val testcontainersVersion = "1.19.3"
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:3.0.1")

    // Ktor specific
    val ktorVersion = "2.3.7"
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // DB
    val jetbrainsExposedVersion = "0.45.0"
    implementation("org.jetbrains.exposed:exposed-core:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$jetbrainsExposedVersion")

    // Mock-oauth2-server
    val mockOAuth2ServerVersion = "2.1.0"
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")

}

tasks {
    withType<Test> {
        dependsOn(":app:shadowJar")
    }
}

kotlin {
    jvmToolchain(17)
}
