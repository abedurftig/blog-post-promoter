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
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.4.2.201908231537-r")
    implementation("org.http4k:http4k-core:3.250.0")
    implementation("org.http4k:http4k-client-apache:3.250.0")
    implementation("io.vavr:vavr:0.10.3")
}
