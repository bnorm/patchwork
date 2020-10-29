@file:JvmName("Patchwork")

package com.bnorm.patchwork

import kotlin.jvm.JvmName

private val lineEndRegex = "\r?\n".toRegex()

fun String?.diff(replacement: String): List<Patch> {
  // TODO handle existing ANSI character
  // TODO does the presence of a \r in a line break diff?

  val originalLines = this?.split(lineEndRegex) ?: emptyList()
  val replacementLines = replacement.split(lineEndRegex)

  val patches = mutableListOf<Patch>()
  val lineCount = maxOf(originalLines.size, replacementLines.size)
  for (l in 0 until lineCount) {
    val originalLine = originalLines.getOrNull(l)
    val replacementLine = replacementLines.getOrNull(l) ?: ""

    if (originalLine == null) {
      patches.add(Patch(l, 0, replacementLine, newline = true))
      continue
    }

    // Find differences within the common regions of the strings
    val characterCount = minOf(originalLine.length, replacementLine.length)
    var c = 0
    while (c < characterCount) {
      if (replacementLine[c] != originalLine[c]) {
        val offset = c
        c++ // seek forward until the next matching character
        while (c < characterCount && replacementLine[c] != originalLine[c]) c++
        patches.add(Patch(l, offset, replacementLine.substring(offset, c)))
      }
      c++
    }

    // Find differences for strings of different sizes
    if (originalLine.length > replacementLine.length) {
      patches.add(Patch(l, replacementLine.length, "", truncate = true))
    } else if (replacementLine.length > originalLine.length) {
      patches.add(Patch(l, originalLine.length, replacementLine.substring(originalLine.length)))
    }

    // Merge differences which cross from within the common region to the end of the line
    if (patches.size >= 2) {
      val ultimate = patches[patches.size - 1]
      val penultimate = patches[patches.size - 2]
      if (ultimate.line == penultimate.line && ultimate.offset == penultimate.offset + penultimate.replacement.length) {
        patches.removeAt(patches.size - 1)
        patches.removeAt(patches.size - 1)
        patches.add(
          Patch(
            penultimate.line,
            penultimate.offset,
            penultimate.replacement + ultimate.replacement,
            truncate = ultimate.truncate
          )
        )
      }
    }
  }

  return patches
}

fun String?.patch(replacement: String): String {
  val patches = diff(replacement)

  val originalLines = (this?.split(lineEndRegex) ?: emptyList()).size
  val replacementLines = replacement.split(lineEndRegex).size

  // TODO sort patches in most efficient way?

  return buildString {
    // Cursor starts at the original ending line
    var cursorLine = originalLines
    var cursorOffset = 0
    for (patch in patches) {
      append(ansiMoveCursor(patch.line - cursorLine, patch.offset - cursorOffset))
      append(patch.replacement)
      if (patch.truncate) append(ansiClear())

      cursorLine = patch.line
      cursorOffset = patch.offset + patch.replacement.length

      if (patch.newline) {
        appendLine()
        cursorLine++
        cursorOffset = 0
      }
    }

    append(ansiMoveCursor(replacementLines - cursorLine, -cursorOffset))
  }
}

