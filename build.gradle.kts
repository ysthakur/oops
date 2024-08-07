plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "io.github.ysthakur"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    from("$rootDir/stdlib").into("$resources/stdlib")
}

spotless {
    java {
        // fix formatting of type annotations
        formatAnnotations()
    }
}
