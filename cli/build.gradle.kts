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
    implementation("io.github.openfeign:feign-slf4j:11.0")
    implementation("io.github.openfeign:feign-okhttp:11.0")
    implementation("io.github.openfeign:feign-gson:11.0")
}
