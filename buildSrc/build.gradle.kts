plugins {
  `kotlin-dsl`
  `kotlin-dsl-precompiled-script-plugins`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
  implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.10.2")
  implementation("com.netflix.nebula:nebula-release-plugin:15.3.0")
  implementation("com.github.jengelman.gradle.plugins:shadow:6.1.0")
}
