val logback_version: String by project
val kotlin_version: String by project
val jackson_version: String by project
val kotest_version: String by project


buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2")
        }

        dependencies {
            classpath(kotlin("gradle-plugin", version = "1.3.72"))
        }

        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}


plugins {
    java
}

allprojects {
    group = "com.example"
    version = "0.0.1"

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        jcenter()
        mavenLocal()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
    }

    dependencies {
        implementation("ch.qos.logback:logback-classic:$logback_version")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
        testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotest_version")
        testImplementation("io.kotest:kotest-assertions-core-jvm:$kotest_version")
        testImplementation("io.kotest:kotest-property-jvm:$kotest_version")
    }


    tasks.withType<Test> {
        useJUnitPlatform()
    }

}


project(":notetaking-restapi") {
    dependencies {
        implementation(project(":notetaking-shared"))
        implementation(project(":dataaccess-service"))
        implementation(project(":repository"))
    }
}


project(":notetaking-web") {
    dependencies {
        implementation(project(":notetaking-shared"))
        implementation(project(":notetaking-service"))
    }
}

project(":dataaccess-service") {
    dependencies {
        implementation(project(":notetaking-shared"))
        implementation(project(":repository"))
    }
}

project(":notetaking-service") {
    dependencies {
        implementation(project(":notetaking-shared"))
    }
}