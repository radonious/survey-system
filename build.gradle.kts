import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "edu.eltex"
version = "0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // Starters
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.0")
    implementation("org.springframework.boot:spring-boot-starter-security:3.4.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.0")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.0")
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.0")

    // Test
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.3")
    testImplementation("org.springframework.security:spring-security-test:6.4.1")
    testImplementation("org.springframework.boot:spring-boot-testcontainers:3.4.0")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.testcontainers:postgresql:1.20.4")

    // Database
    implementation("org.liquibase:liquibase-core:4.30.0")
    runtimeOnly("org.postgresql:postgresql:42.7.4")

    //Security
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Libs
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    implementation("org.apache.poi:poi-ooxml:5.3.0")
    implementation("org.apache.tika:tika-core:2.9.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("buildUI") {
    group = "custom"
    description = "Build UI and copy into static resources"

    val distFolder by extra { file("ui/dist") }
    val uiFolder by extra { file("ui") }
    val env = file(".env")

    doLast {
        if (!env.exists()) {
            throw GradleException("No .env in root folder")
        } else if (!uiFolder.exists()) {
            throw GradleException("No ui/ in root folder")
        }

        if (distFolder.exists()) {
            println("[GRADLE TASKS] - ui/dist folder exist. Deleting...")
            delete(distFolder)
        }

        exec {
            workingDir = uiFolder
            commandLine("npm", "install")
        }

        exec {
            workingDir = uiFolder
            commandLine("npm", "fund")
        }

        exec {
            workingDir = uiFolder
            commandLine("npm", "run", "build")
        }

        if (!distFolder.exists()) {
            throw GradleException("Folder 'ui/dist' did not created. UI build problem.")
        }

        println("[GRADLE TASKS] - UI built successfully.")
    }


    finalizedBy("copyToResources")
}


tasks.register("copyToResources") {
    group = "custom"
    description = "Copy data"

    dependsOn("buildUI")

    doLast {
        val uiDist = file("ui/dist")
        val resourcesStatic = file("src/main/resources/static")

        if (!resourcesStatic.exists()) {
            println("[GRADLE TASKS] - resources/static folder does not exist. Creating...")
            resourcesStatic.mkdirs()
        } else {
            println("[GRADLE TASKS] - resources/static folder exist. Clearing...")
            delete(resourcesStatic)
            resourcesStatic.mkdirs()
        }

        project.copy {
            from(uiDist)
            into(resourcesStatic)
        }

        println("[GRADLE TASKS] - UI dist copied successfully.")
    }
}

tasks.findByName("bootRun")?.dependsOn("buildUI")
tasks.findByName("bootBuildImage")?.dependsOn("buildUI")