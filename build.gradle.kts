import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
}

group = "net.doemges"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

extra["springModulithVersion"] = "1.1.4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-web
    implementation("org.springframework.security:spring-security-web")
    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-config
    implementation("org.springframework.security:spring-security-config")

    // https://mvnrepository.com/artifact/com.github.kokorin.jaffree/jaffree
    implementation("com.github.kokorin.jaffree:jaffree:2023.09.10")

    // https://mvnrepository.com/artifact/org.bytedeco/opencv
    implementation("org.bytedeco:opencv:4.9.0-1.5.10")

    // https://mvnrepository.com/artifact/org.bytedeco/javacv
    implementation("org.bytedeco:javacv:1.5.10")

    // https://mvnrepository.com/artifact/org.bytedeco/javacpp
    implementation("org.bytedeco:javacpp:1.5.10")

    // https://mvnrepository.com/artifact/org.bytedeco/openblas
    implementation("org.bytedeco:openblas:0.3.26-1.5.10")


    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-oauth2-client
    implementation("org.springframework.security:spring-security-oauth2-client:6.3.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = listOf("-Djava.library.path=/opt/homebrew/opt/openblas/lib")
}
