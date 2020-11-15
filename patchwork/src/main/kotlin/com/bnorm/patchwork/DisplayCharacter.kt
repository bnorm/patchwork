package com.bnorm.patchwork

internal fun String.asDisplayIterator(): Iterator<DisplayCharacter> = iterator {
  val upstream = this@asDisplayIterator
  var nextStyle = ansiColorEscape.find(upstream, 0)

  var index = 0
  var offset = 0
  var line = 0
  var style: String? = null
  while (index < upstream.length) {
    if (nextStyle != null && index == nextStyle.range.first) {
      style = nextStyle.value
      index = nextStyle.range.last + 1
      nextStyle = nextStyle.next()
    } else {
      val codePoint = upstream.codePointAt(index)
      index += codePoint.charCount

      when (codePoint) {
        '\n'.toInt() -> {
          offset = 0
          line++
        }
        '\r'.toInt() -> {
          // TODO expect \n next
        }
        else -> {
          yield(
            DisplayCharacter(
              codePoint = codePoint,
              offset = offset,
              line = line,
              style = style.takeIf { it != "\u001b[0m" },
            )
          )
          offset++
        }
      }
    }
  }
}

internal data class DisplayCharacter(
  val codePoint: Int,
  val offset: Int,
  val line: Int,
  val style: String?
)
