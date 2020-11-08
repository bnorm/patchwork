package com.bnorm.patchwork

internal const val ANSI_ESCAPE = "\u001B["

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
