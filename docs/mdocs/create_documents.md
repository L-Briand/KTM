# Creating documents

### Note about memory management

The `MDocument` object you receive contains a representation of full mustache
template that you have parsed. If you parse a 1Mb template, you will have ~1Mb
memory taken by the object.

## On multiplatform

### Parsing a string

The default way is to parse a document from a string.

```kotlin
val template = "Hello {{ world }}"
val document: MDocument = Ktm.doc.string(template)
```

### Parsing with a custom provider

The parser can read documents from any `CharStream` implementation. As long as you
can provide a flow of character for the parser, it will produce a `MDocument`.

```kotlin
fun interface CharStream {
    fun read(): Char
}
```

For example, here is the implementation of `CharStream` used inside
the `Ktm.doc.string` method:

```kotlin
class StringCharStream(val content: CharSequence) : CharStream {
    private var index = 0
    override fun read(): Char = if (index < content.length) {
        val result = content[index]
        index += 1
        result
    } else Char.MAX_VALUE
}
```

You can then use :

```kotlin
val template = "Hello {{ world }}"
val document = Ktm.doc.charStream(StringCharStream(template))
```

### Create extensions

Now that you have your custom `CharStream` implementation. You can add your own
function to the `ktm.doc` builder.

You can create a function that fits your needs. Like, creating your own
caching system or your own reader.

For example, this is how the `Ktm.doc.file` function *could* be defined for the JVM:

```kotlin
fun DocumentBuilder.file(file: File): MDocument? {
    if (!file.exists() && !file.isFile()) return null
    return string(file.readText())
}

val document = Ktm.doc.file(File("my/file.mustache"))
```

## Jvm only

### With a file or path

```kotlin
var document: MDocument

document = Ktm.doc.file(File("my/file.mustache"), Charsets.UTF_8)
document = Ktm.doc.path(Path.of("my", "file.mustache"), Charsets.UTF_16)
```

#### With java IO streams

```kotlin
val reader: Reader
val document = reader.use { Ktm.doc.fromReader(it) }
```

```kotlin
val stream: InputStream
val document = stream.use { Ktm.doc.fromInputStream(it) }
```