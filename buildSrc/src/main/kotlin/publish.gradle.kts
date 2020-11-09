import org.gradle.api.plugins.JavaBasePlugin.DOCUMENTATION_GROUP

plugins {
  `maven-publish`
  signing
  id("org.jetbrains.dokka")
}

group = "com.bnorm.patchwork"

val projectUrl = "github.com/bnorm/patchwork"
val sonatypeSnapshotUrl = "https://oss.sonatype.org/content/repositories/snapshots"
val sonatypeReleaseUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"

repositories {
  jcenter()
}

val release = tasks.findByPath(":release")
release?.finalizedBy(tasks.publish)

val dokkaJar by tasks.creating(Jar::class) {
  group = DOCUMENTATION_GROUP
  description = "Assembles Kotlin docs with Dokka"
  archiveClassifier.set("javadoc")
  from(tasks["dokkaHtml"])
}

signing {
  val signingKey = findProperty("signingKey") as? String
  val signingPassword = (findProperty("signingPassword") as? String).orEmpty()
  if (signingKey != null) {
    useInMemoryPgpKeys(signingKey, signingPassword)
  }

  setRequired(provider { gradle.taskGraph.hasTask("release") })
  sign(publishing.publications)
}

publishing {
  publications {
    create<MavenPublication>("default") {
      from(components["java"])
      artifact(dokkaJar)

      pom {
        name.set("Patchwork")
        description.set("Library for generating ANSI patches for updating existing console output")
        url.set("https://$projectUrl")

        licenses {
          license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        scm {
          url.set("https://$projectUrl")
          connection.set("scm:git:git://$projectUrl.git")
        }
        developers {
          developer {
            name.set("Brian Norman")
            url.set("https://github.com/bnorm")
          }
        }
      }
    }
  }

  repositories {
    if (hasProperty("sonatypeUsername") && hasProperty("sonatypePassword")) {
      maven {
        setUrl(if ("SNAPSHOT" in version.toString()) sonatypeSnapshotUrl else sonatypeReleaseUrl)
        credentials {
          username = property("sonatypeUsername") as String
          password = property("sonatypePassword") as String
        }
      }
    }
  }
}
