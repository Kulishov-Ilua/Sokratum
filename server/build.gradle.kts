plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    application
}

group = "ru.kulishov.sokratum"
version = "1.0.0"
application {
    mainClass.set("ru.kulishov.sokratum.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
    implementation(libs.langchain4j)
    implementation(libs.langchain4j.open.ai)
    implementation("dev.langchain4j:langchain4j-local-ai:1.11.0-beta19")
    implementation("dev.langchain4j:langchain4j-easy-rag:1.11.0-beta19")
    implementation("dev.langchain4j:langchain4j-agentic:1.13.0-beta23")
    implementation("dev.langchain4j:langchain4j-vertex-ai-gemini:1.13.1-beta23")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini:1.13.1")
    implementation("chat.giga:langchain4j-gigachat:0.1.20")

    implementation("io.ktor:ktor-server-cors:${ktor}")

    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.ktor.server.config.yaml)

    implementation("org.jetbrains.exposed:exposed-core:1.1.1")
    implementation("org.jetbrains.exposed:exposed-dao:1.1.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.1.1")
    implementation("org.jetbrains.exposed:exposed-java-time:1.1.1")
    implementation("org.jetbrains.exposed:exposed-crypt:1.1.1")




}