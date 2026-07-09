plugins {
    `java-library`
    `maven-publish`
}

group = "com.github.lukesky19"
version = "0.2.2.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://maven.devs.beer/")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")

    // SkyLib
    compileOnly("com.github.lukesky19:SkyLib:2.0.2.0")

    // Integration
    compileOnly("world.bentobox:bentobox:2.7.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.17-SNAPSHOT") {
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.guava")
    }
    compileOnly("dev.rosewood:rosestacker:1.5.41")
    compileOnly("dev.lone:api-itemsadder:4.0.10")
    compileOnly("com.github.lukesky19:SkyHoppers:1.4.1.0")
    compileOnly("com.github.lukesky19:SkyShop:3.3.1.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    javadoc {
        source = sourceSets["main"].allJava
        classpath = files() + configurations["compileClasspath"]

        (options as StandardJavadocDocletOptions).apply {
            tags("apiNote:a:API Note:")
            addStringOption("sourcepath", "")
        }
    }

    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveClassifier.set("")
    }

    build {
        dependsOn(javadoc)
        dependsOn(publishToMavenLocal)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}