import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.7.20"
  application
}

group = "com.alexelkin.pws_library_manager"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  //  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

application {
  mainClass.set("MainKt")
}