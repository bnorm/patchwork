package com.bnorm.patchwork

import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class PatchworkAnsiTest {
  @Test
  fun testDiffOfSingleLine() {
    val patches = "\u001b[32msome\u001b[0m thing".diff("\u001b[32msome-thing\u001b[0m")
    val expected = listOf(
      Patch(0, 4, "\u001b[32m-thing\u001B[0m")
    )
    assertEquals(expected, patches)
  }

  @Test
  fun testDiffOfShorterSingleLine() {
    val patches = "some thing".diff("some")
    val expected = listOf(
      Patch(0, 4, "", truncate = true)
    )
    assertEquals(expected, patches)
  }

  @Test
  fun testDiffOfLongerSingleLine() {
    val patches = "some thing".diff("something else")
    val expected = listOf(
      Patch(0, 4, "thing else")
    )
    assertEquals(expected, patches)
    Instant.now().truncatedTo(ChronoUnit.MILLIS)
  }

  @Test
  fun testDiffOfMultiLine() {
    val patches = """
      [32msome thing[0m
      patch work
    """.trimIndent().diff("""
      [32msome-thing[0m
      patch-werk
    """.trimIndent())
    val expected = listOf(
      Patch(0, 4, "\u001B[32m-\u001B[0m"),
      Patch(1, 5, "-"),
      Patch(1, 7, "e"),
    )
    assertEquals(expected, patches)
  }

  @Test
  fun testDiffOfMultiLineAddLine() {
    val patches = """
      some thing
      [32mpatch work
    """.trimIndent().diff("""
      some-thing
      [32mpatch-werk
      hello, world
    """.trimIndent())
    val expected = listOf(
      Patch(0, 4, "-"),
      Patch(1, 5, "\u001B[32m-\u001B[0m"),
      Patch(1, 7, "\u001B[32me\u001B[0m"),
      Patch(2, 0, "\u001B[32mhello, world\u001B[0m", newline = true),
    )
    assertEquals(expected, patches)
  }
}
