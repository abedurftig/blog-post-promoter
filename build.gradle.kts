buildscript {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(Plugins.kotlinGradlePlugin)
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.kapt") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("io.gitlab.arturbosch.detekt") version "1.0.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    jacoco
}

allprojects {

    version = "0.0.1"
    group = "blog-post-promoter"

    repositories {
        jcenter()
        mavenCentral()
    }
}

subprojects {

    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "jacoco")

    ktlint {
        version.set("0.36.0")
        // See https://github.com/pinterest/ktlint/issues/527
        disabledRules.set(setOf("import-ordering"))
    }

    dependencies {

        implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        implementation("org.slf4j:slf4j-api:1.7.30")
        implementation("org.slf4j:slf4j-log4j12:1.7.30")

        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
        testImplementation("org.assertj:assertj-core:3.12.2")
        testImplementation("org.mockito:mockito-core:2.28.2")
        testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")

        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = "11"
                javaParameters = true
            }
        }
        compileTestKotlin {
            kotlinOptions {
                jvmTarget = "11"
                javaParameters = true
            }
        }
        test {
            useJUnitPlatform()
        }
        jacocoTestReport {
            reports {
                xml.isEnabled = false
                csv.isEnabled = false
                html.isEnabled = true
                html.destination = file("$buildDir/reports/coverage")
            }
        }
        shadowJar {
            mergeServiceFiles()
        }
        check {
            dependsOn("jacocoTestReport")
        }
    }
}
