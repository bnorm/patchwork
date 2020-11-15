@file:JvmName("Patchwork")

package com.bnorm.patchwork

import kotlin.jvm.JvmName

private val lineEndRegex = "\r?\n".toRegex()

fun String?.diff(replacement: String): List<Patch> {
  val originalIterator = (this ?: "").asDisplayIterator()
  val replacementIterator = replacement.asDisplayIterator()

  val patches = mutableListOf<Patch>()

  val builder = StringBuilder()
  var line = 0
  var offset = 0
  var style: String? = null

  fun reset(current: DisplayCharacter) {
    line = current.line
    offset = current.offset
    style = null
    builder.clear()
  }

  fun StringBuilder.appendDisplayCharacter(displayCharacter: DisplayCharacter) {
    if (style != displayCharacter.style) {
      append(displayCharacter.style ?: "\u001b[0m")
      style = displayCharacter.style
    }
    appendCodePoint(displayCharacter.codePoint)
  }

  fun addPatch(truncate: Boolean = false, newline: Boolean = false) {
    if (style != null) builder.append("\u001b[0m")
    patches.add(Patch(line, offset, builder.toString(), truncate = truncate, newline = newline))
  }

  while (originalIterator.hasNext() && replacementIterator.hasNext()) {
    var originalDisplay = originalIterator.next()
    var replacementDisplay = replacementIterator.next()

    // If the replacement line is longer than the original
    if (replacementDisplay.line < originalDisplay.line) {
      if (builder.isEmpty()) reset(replacementDisplay)

      while (replacementDisplay.line < originalDisplay.line) {
        builder.appendDisplayCharacter(replacementDisplay)
        replacementDisplay = replacementIterator.next()
      }

      addPatch()
      reset(replacementDisplay)
    }

    // If the original line is longer than the replacement
    else if (replacementDisplay.line > originalDisplay.line) {
      if (builder.isEmpty()) reset(originalDisplay)

      while (replacementDisplay.line > originalDisplay.line) {
        originalDisplay = originalIterator.next()
      }

      addPatch(truncate = true)
      reset(replacementDisplay)
    }

    // It is now the start of a new line
    if (replacementDisplay.line > line && builder.isNotEmpty()) {
      addPatch()
      reset(replacementDisplay)
    }

    // The code point or style differs
    if (replacementDisplay.codePoint != originalDisplay.codePoint ||
      replacementDisplay.style != originalDisplay.style
    ) {
      if (builder.isEmpty()) reset(replacementDisplay)
      builder.appendDisplayCharacter(replacementDisplay)
    }

    // No difference but previous difference
    else if (builder.isNotEmpty()) {
      addPatch()
      reset(replacementDisplay)
    }

    // Continues to be no difference
    else {
      reset(replacementDisplay)
    }
  }

  val truncate = originalIterator.hasNext()
  if (originalIterator.hasNext()) {
    val first = originalIterator.next()
    if (first.line == line) {
      if (builder.isEmpty()) {
        offset = first.offset
      }
      addPatch(truncate = true)
      reset(first)
      offset = 0
    }
    while (originalIterator.hasNext()) {
      val originalDisplay = originalIterator.next()
      if (originalDisplay.line != line) {
        patches.add(Patch(originalDisplay.line, 0, "", truncate = true))
        line = originalDisplay.line
      }
    }
  }

  val startLine = line
  while (replacementIterator.hasNext()) {
    val replacementDisplay = replacementIterator.next()

    if (replacementDisplay.line > line && builder.isNotEmpty()) {
      addPatch(newline = true)
      reset(replacementDisplay)
    }
    if (builder.isEmpty()) reset(replacementDisplay)
    builder.appendDisplayCharacter(replacementDisplay)
  }

  if (builder.isNotEmpty()) {
    addPatch(truncate = truncate, newline = line > startLine)
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
      cursorOffset = patch.offset + patch.replacement.visualCodePointCount

      if (patch.newline) {
        appendLine()
        cursorLine++
        cursorOffset = 0
      }
    }

    append(ansiMoveCursor(replacementLines - cursorLine, -cursorOffset))
  }
}
