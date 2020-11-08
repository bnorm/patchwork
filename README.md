# patchwork

[![Maven Central][maven-central-badge]][maven-central-link]

Create ANSI patches of text strings which can be printed to a console to update
existing text. This library supports multiline output which allows for
combination with all sorts of text based console output.

## Examples

### Progress Bar

To display some kind of progress bar in the console as a task is being
performed, the following pattern can be used.

```kotlin
fun progressBarString(progress: Int): String = ...

var previous: String? = null
while (progress in 1..100) {
  val next = progressBarString(progress)
  print(previous.patch(next))
  previous = next
}
```

### Logging With Progress Bar

To display some kind of progress bar in the console while also logging text,
you can use the following pattern.

```kotlin
fun progressBarString(progress: Int): String = ...

var previous: String? = null
while (progress in 1..100) {
  val log = "... lines you would like logged ..."
  val next = progressBarString(progress)
  print(previous.patch(log + next))
  previous = next
}
```

### Table Progress

To display more complex messages you can use a library like
[picnic](https://github.com/JakeWharton/picnic) to generate a user-friendly
table which can display progress for multiple things at the same time. See the
sample project for an [example][picnic-example].

![picnic-example](docs/picnic-example.svg)

## Platforms

Some platforms (Windows) do not natively support ANSI text manipulation. Some
third-party libraries like [jansi](https://github.com/fusesource/jansi) can add
this support through platform specific APIs.

## What's Next?

- ANSI Colors: ANSI text styling really messes with the diff calculation. Text
  changes in the middle of styled text will also require tracking the style of
  both text strings and applying the correct style in the patched text.

[maven-central-badge]: https://maven-badges.herokuapp.com/maven-central/com.bnorm.patchwork/patchwork/badge.svg
[maven-central-link]: https://maven-badges.herokuapp.com/maven-central/com.bnorm.patchwork/patchwork
[picnic-example]: https://github.com/bnorm/patchwork/blob/main/sample/src/main/kotlin/main.kt
