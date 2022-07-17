plugins {
    id("org.jetbrains.intellij") version "1.7.0"
    id("org.jetbrains.kotlin.jvm") version "1.7.0"
}

group = "branch-manager"
version = "1.1.5"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

intellij {
    version.set("2022.1.2")
    plugins.set(listOf("git4idea"))
}

tasks {
    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("221")
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}