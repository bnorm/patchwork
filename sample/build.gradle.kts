plugins {
  kotlin("jvm")
  id("binary")
}

dependencies {
  implementation(project(":patchwork"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.0")
}

application {
  mainClass.set("MainKt")
  mainClassName = mainClass.get()
}
