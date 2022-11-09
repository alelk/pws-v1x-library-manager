plugins {
  id("org.jetbrains.kotlin.plugin.noarg") version "1.7.21"
  kotlin("jvm")
  id("java")
}

group = "com.alexelkin.pws_library_manager"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  // Jackson XML
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.0") // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0") // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin

  // Test dependencies
  testImplementation(kotlin("test"))
  testImplementation("io.kotest:kotest-runner-junit5:5.5.0")
  testImplementation("io.kotest:kotest-assertions-core:5.5.0")
  testImplementation("io.kotest:kotest-property:5.5.0")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

noArg {
  annotation("com.alexelkin.pws_library_manager.core.util.NoArg")
}