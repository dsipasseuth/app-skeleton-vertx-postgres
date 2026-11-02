plugins {
    application
    kotlin("jvm") version "2.3.0"
    id("io.micronaut.application") version "5.0.2"
    id("com.google.devtools.ksp") version "2.3.0"
    id("com.google.protobuf") version "0.9.6"
    id("com.diffplug.spotless") version "7.0.2"
}

group = "com.skel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object Versions {
    val protobuf = "4.29.3"
    val ioGrpc = "1.70.0"
    val protoTruth = "1.4.4"
    val vertx = "5.2.2"
    val kotest = "5.9.1"
    var logback = "1.5.16"
    val mutiny = "4.1.0"
}

dependencies {
    // Protobuf & gRPC (Removed grpc-kotlin-stub)
    implementation("com.google.protobuf:protobuf-kotlin:${Versions.protobuf}")
    implementation("io.grpc:grpc-protobuf:${Versions.ioGrpc}")

    // Vert.x Core, gRPC, & PG Client (Removed duplicate vertx-pg-client & coroutines)
    implementation("io.vertx:vertx-core:${Versions.vertx}")
    implementation("io.smallrye.reactive:smallrye-mutiny-vertx-core:${Versions.mutiny}")
    implementation("io.smallrye.reactive:smallrye-mutiny-vertx-pg-client:${Versions.mutiny}")

    implementation("io.vertx:vertx-grpc-server:${Versions.vertx}")
    implementation("io.vertx:vertx-grpc-client:${Versions.vertx}")
    implementation("io.vertx:vertx-grpc-reflection:${Versions.vertx}")
    implementation("io.vertx:vertx-lang-kotlin:${Versions.vertx}")

    // Micronaut DI & Validation
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut.validation:micronaut-validation")
    ksp("io.micronaut.validation:micronaut-validation-processor")

    // Test Dependencies
    testImplementation(kotlin("test"))
    testImplementation("com.google.truth.extensions:truth-proto-extension:${Versions.protoTruth}")
    testImplementation("com.google.truth:truth:${Versions.protoTruth}")
    testImplementation("io.vertx:vertx-junit5:${Versions.vertx}")
    testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
    testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
    testImplementation("io.mockk:mockk:1.13.16")

    // Runtime Configuration & Logging
    runtimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")
    runtimeOnly("org.yaml:snakeyaml")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}

sourceSets {
    main {
        java.srcDirs(
            "build/generated/sources/proto/main/java",
            "build/generated/sources/proto/main/vertx"
        )
        kotlin.srcDirs(
            "build/generated/sources/proto/main/kotlin"
        )
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.protobuf}"
    }
    plugins {
        create("vertx") {
            artifact = "io.vertx:vertx-grpc-protoc-plugin2:${Versions.vertx}"
        }
    }
    generateProtoTasks {
        all().configureEach {
            plugins {
                create("vertx") {}
            }
            builtins {
                named("java") {}
                create("kotlin") {}
            }
        }
    }
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktfmt()
    }
    java {
        targetExclude("**/generated/**/*.java")
        googleJavaFormat()
    }
}

application {
    // Point this to the file containing your main() function (e.g., MainKt if it's Main.kt)
    mainClass.set("com.skel.MainKt")
}

micronaut {
    version.set("5.0.2")
    runtime.set(io.micronaut.gradle.MicronautRuntime.NETTY)
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.skel.*")
    }
}