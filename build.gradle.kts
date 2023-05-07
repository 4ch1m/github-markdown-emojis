import org.jetbrains.changelog.Changelog

fun properties(key: String) = project.findProperty(key).toString()

version = properties("pluginVersion")
description = properties("pluginDescription")

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.13.3"
    id("org.jetbrains.changelog") version "2.0.0"
    id("com.github.ben-manes.versions") version "0.46.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.beust:klaxon:5.6")  {
        exclude(group = "org.jetbrains.kotlin") // prefer Kotlin distribution offered by IDE
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
}

intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    plugins.set(properties("platformPlugins").split(",").map(String::trim).filter(String::isNotEmpty))
    updateSinceUntilBuild.set(false)
}

changelog {
    version.set(properties("pluginVersion"))
}

tasks {
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = properties("kotlinJvmTarget")
    }

    withType<Test> {
        useJUnitPlatform()
    }

    dependencyUpdates {
        rejectVersionIf {
            (
                listOf("RELEASE", "FINAL", "GA").any { candidate.version.uppercase().contains(it) }
                ||
                "^[0-9,.v-]+(-r)?$".toRegex().matches(candidate.version)
            ).not()
        }
    }

    patchPluginXml {
        pluginDescription.set(properties("pluginDescription"))
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        changeNotes.set(provider {
            changelog.renderItem(
                changelog.getLatest(),
                Changelog.OutputType.HTML
            )
        })
    }

    publishPlugin {
        if (project.hasProperty("JB_PLUGIN_PUBLISH_TOKEN")) {
            token.set(project.property("JB_PLUGIN_PUBLISH_TOKEN").toString())
        }
    }
}
