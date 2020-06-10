buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    application
}

application {
    mainClassName = "org.abedurftig.promoter.Application"
}

dependencies {
    implementation("commons-codec:commons-codec:1.14")
    implementation("com.github.ajalt:clikt:2.7.1")
}
