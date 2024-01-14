import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("org.jmailen.kotlinter") version "4.2.0"
    `java-library`
    `maven-publish`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
    }
}

val githubRepo = project.property("github.repo") as String

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8")

    api(group = "com.squareup.okhttp3", name = "okhttp", version = "5.0.0-alpha.12")

    api(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.6.2")
    api(group = "io.github.pdvrieze.xmlutil", name = "serialization-jvm", version = "0.86.2")

    // Use the Kotlin test library.
    testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-test-junit")
}

val sourcesJar by tasks.registering(Jar::class) {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/$githubRepo")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GH_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GH_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("alsong-kt") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}
