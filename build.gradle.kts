plugins {
    id("org.jetbrains.intellij") version "1.13.3"
    id("org.jetbrains.kotlin.jvm") version "1.7.0"
}

group = "branch-manager"
version = "1.1.7"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

intellij {
    version.set("2023.1")
    plugins.set(listOf("Git4Idea"))
}

tasks {
    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("231")
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}