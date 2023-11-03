plugins {
    id("java-library")
}

group = "com.github.ynverxe"
version = "0.0.1-beta"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.Minestom.Minestom:Minestom:c496ee357")
}