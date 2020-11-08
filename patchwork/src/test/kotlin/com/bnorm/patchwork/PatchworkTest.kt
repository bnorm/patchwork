package com.bnorm.patchwork

import kotlin.test.Test
import kotlin.test.assertEquals

class PatchworkTest {
  @Test
  fun testDiffOfSingleLine() {
    val patches = "some thing".diff("some-thing")
    val expected = listOf(
      Patch(0, 4, "-")
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
  }

  @Test
  fun testDiffOfMultiLine() {
    val patches = """
      some thing
      patch work
    """.trimIndent().diff("""
      some-thing
      patch-werk
    """.trimIndent())
    val expected = listOf(
      Patch(0, 4, "-"),
      Patch(1, 5, "-"),
      Patch(1, 7, "e"),
    )
    assertEquals(expected, patches)
  }

  @Test
  fun testDiffOfMultiLineAddLine() {
    val patches = """
      some thing
      patch work
    """.trimIndent().diff("""
      some-thing
      patch-werk
      hello, world
    """.trimIndent())
    val expected = listOf(
      Patch(0, 4, "-"),
      Patch(1, 5, "-"),
      Patch(1, 7, "e"),
      Patch(2, 0, "hello, world", newline = true),
    )
    assertEquals(expected, patches)
  }
}
