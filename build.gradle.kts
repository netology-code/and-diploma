plugins {
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    val kotlinVersion = "2.3.0"
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.flywaydb.flyway") version "12.0.1"
}

flyway {
    url = System.getenv("SPRING_DATASOURCE_URL")
    user = System.getenv("SPRING_DATASOURCE_USERNAME")
    password = System.getenv("SPRING_DATASOURCE_PASSWORD")
    baselineOnMigrate = true
    locations = arrayOf("filesystem:resources/db/migration")
}

group = "ru.netology"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.github.imagekit-developer:imagekit-java:2.0.1")
    implementation("org.apache.tika:tika-core:3.2.3")
    runtimeOnly("org.postgresql:postgresql:42.7.10")
    implementation("org.bouncycastle:bctls-jdk15on:1.70")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    implementation("org.flywaydb:flyway-core:12.0.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
