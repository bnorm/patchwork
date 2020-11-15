plugins {
  kotlin("jvm")
  id("binary")
}

dependencies {
  implementation(project(":patchwork"))
  implementation("com.jakewharton.picnic:picnic:0.5.0")
  implementation("com.github.ajalt:mordant:1.2.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.0")
}

application {
  mainClass.set("MainKt")
  mainClassName = mainClass.get()
}
