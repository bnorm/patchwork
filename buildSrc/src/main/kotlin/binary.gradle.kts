plugins {
  application
  id("com.github.johnrengelman.shadow")
}

val binaryFile = File("$buildDir/${base.archivesBaseName}.jar")
val binaryJarProvider = tasks.register("binaryJar") {
  val shadowJarProvider = tasks.named<Jar>("shadowJar")
  dependsOn(shadowJarProvider)

  val fatJar = shadowJarProvider.get().archiveFile.get().asFile
  inputs.file(fatJar)
  outputs.file(binaryFile)

  doLast {
    binaryFile.parentFile.mkdirs()
    binaryFile.delete()
    binaryFile.appendText("#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n")
    fatJar.inputStream().use {
      binaryFile.appendBytes(it.readBytes())
    }

    binaryFile.setExecutable(true, false)
  }
}
