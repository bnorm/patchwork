package com.bnorm.patchwork

class Patch(
  val line: Int,
  val offset: Int,
  val replacement: String,
  val truncate: Boolean = false,
  val newline: Boolean = false,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as Patch

    if (line != other.line) return false
    if (offset != other.offset) return false
    if (replacement != other.replacement) return false
    if (truncate != other.truncate) return false
    if (newline != other.newline) return false

    return true
  }

  override fun hashCode(): Int {
    var result = line
    result = 31 * result + offset
    result = 31 * result + replacement.hashCode()
    result = 31 * result + truncate.hashCode()
    result = 31 * result + newline.hashCode()
    return result
  }

  override fun toString(): String {
    return "Patch(line=$line, offset=$offset, replacement='$replacement', truncate=$truncate, newline=$newline)"
  }
}
