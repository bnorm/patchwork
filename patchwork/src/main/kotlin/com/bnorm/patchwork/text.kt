package com.bnorm.patchwork

private const val ANSI_ESCAPE = "\u001B["
private val ansiColorEscape = Regex("""\u001B\[\d+(;\d+)*m""")

internal fun ansiMoveCursor(vertical: Int, horizontal: Int): String {
  if (vertical == 0 && horizontal == 0) return ""
  return buildString {
    if (vertical != 0) {
      append(ANSI_ESCAPE)
      if (vertical < 0) append("${-vertical}A") // up
      else append("${vertical}B") // down
    }
    if (horizontal != 0) {
      append(ANSI_ESCAPE)
      if (horizontal > 0) append("${horizontal}C") // right
      else append("${-horizontal}D") // left
    }
  }
}

internal fun ansiClear(): String = "${ANSI_ESCAPE}K"

internal fun String.visibleAt(index: Int): Int {
  return substring(0, index).visualCodePointCount
}

internal val CharSequence.visualCodePointCount: Int get() {
  // Fast path: no escapes.
  val firstEscape = indexOf('\u001B')
  if (firstEscape == -1) {
    return Character.codePointCount(this, 0, length)
  }

  var currentIndex = firstEscape
  var count = Character.codePointCount(this, 0, firstEscape)
  while (true) {
    val match = ansiColorEscape.find(this, startIndex = currentIndex) ?: break
    count += Character.codePointCount(this, currentIndex, match.range.first)
    currentIndex = match.range.last + 1
  }
  count += Character.codePointCount(this, currentIndex, length)
  return count
}
