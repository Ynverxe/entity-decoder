plugins {
    id("maven-publish")
    id("java-library")
}

group = "com.github.ynverxe"
version = "0.0.1-beta"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("dev.hollowcube:minestom-ce:5bcc72b911")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}