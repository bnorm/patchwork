package com.bnorm.patchwork

import com.jakewharton.picnic.table
import kotlinx.coroutines.delay
import kotlin.math.pow

suspend fun main() {
  var display: String? = null
  repeat(100) { i ->
    val next = display(i + 4)
    print(display.patch(next))
    display = next
//    delay(100)
  }
}

fun display(i: Int) = table {
  cellStyle {
    border = true
    paddingLeft = 1
    paddingRight = 1
  }

  header { row("var", "value") }
  row("i", 2.0.pow(i))
  row("loading", "<${"=".repeat(i)}>")
}.toString()
