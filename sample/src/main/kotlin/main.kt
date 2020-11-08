import kotlinx.coroutines.delay
import kotlin.random.Random
import com.bnorm.patchwork.patch
import com.jakewharton.picnic.BorderStyle
import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.TreeMap

data class Task(val name: String, val progress: Int)

suspend fun main() {
  var previous: String? = null
  val tasks = TreeMap<String, Task>()
  doTasks(4).collect { task ->
    tasks[task.name] = task
    val next = display(tasks)
    print(previous.patch(next))
    previous = next
  }
}

fun doTasks(count: Int) = channelFlow {
  require(count > 0)
  repeat(count) { task ->
    launch {
      for (progress in 1..100) {
        delay(Random.nextLong(250))
        send(Task("Task $task", progress))
      }
    }
  }
}

private fun display(tasks: Map<String, Task>) = table {
  style {
    borderStyle = BorderStyle.Hidden
  }
  cellStyle {
    paddingLeft = 1
    paddingRight = 1
    borderLeft = true
    borderRight = true
  }

  header {
    cellStyle {
      border = true
      alignment = TextAlignment.BottomCenter
    }
    row("Name", "Status", "Progress")
  }

  for ((_, task) in tasks) {
    val status = if (task.progress != 100) "Loading" else "Complete"
    row(task.name, status, progress(task.progress.toDouble() / 100))
  }
}.toString()

fun progress(progress: Double) = buildString {
  val percent = (progress * 100).toInt()
  append("$percent%".padStart(4))
  append(" [")
  repeat(percent / 5) { append('=') }
  if (percent != 100) {
    append(">")
  } else {
    append("]")
  }
}
