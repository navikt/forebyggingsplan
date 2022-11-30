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

    val kotestVerstion = "5.4.2"
    testImplementation("io.kotest:kotest-assertions-core:$kotestVerstion")

    // Funksjonelle operatorer
    implementation("io.arrow-kt:arrow-core:1.1.2")

    // Testcontainers
    val testcontainersVersion = "1.17.6"
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:2.35.0")

    // Ktor specific
    val ktor_version = "2.1.3"
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    testImplementation("io.ktor:ktor-client-core:$ktor_version")
    testImplementation("io.ktor:ktor-client-cio:$ktor_version")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")


    // Mock-oauth2-server
    val mockOAuth2ServerVersion = "0.5.6"
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")

}

tasks {
    withType<Test>{
        dependsOn(":app:shadowJar")
    }
}
