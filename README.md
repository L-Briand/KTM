# KTM

A [Mustache](https://mustache.github.io) implementation in pure Kotlin Multiplatform.

```kotlin
val document = Ktm.doc.string("Hello {{ name }}")
val context = Ktm.ctx.make {
    name by "Jon"
}
val render = document.render(context)
assert("Hello Jon" == render)
```

## Development notice

This tool is still in the backing. I work on it when I have the time. Feel free to
contribute 🙂

- **[List of TODO's](TODO.MD)**

# Import from maven

## Multiplatform

```kotlin
repositories {
    mavenCentral()
}
val commonMain by getting {
    dependencies {
        implementation("net.orandja.ktm:core:0.1.0")
    }
}
```

## Jvm

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("net.orandja.ktm:core:0.1.0")
}
```

# QuickStart

> [!NOTE]
> You cannot use classes as context to render a document yet. This is still in the
> backing. This means you would not be able to use it as a drop-in replacement of
> another mustache lib. You need to create all your contexts manually.
>
> This is **not yet possible**:
>
> ```kotlin
> data class User(val name: String, val age: Int)
> 
> val document = Ktm.doc.string("Hello {{ name }} ! Happy {{ age }} birthday !")
> val context = User("Jon", 33)
> document.render(context)
> ```

With Mustache, to render a document you need three things:

### 1. A template

Something like `Hello {{ name }}`. This needs to be parsed to be used. Use
the `Ktm.doc` object to read and parse the documents into a usable object. If you are
running kotlin JVM, you can use extension functions to parse files and IO
with `.file(File)`, `.path(Path)`, `.resource(String)`, `.inputStream(InputStream)`
or `.reader(Reader)`.

```kotlin
val mustacheTemplate: String = "Hello {{ name }}"
var document: MDocument
// default way
document = Ktm.doc.string(mustacheTemplate)
// quick way, only work on strings
document = mustacheTemplate.toMustacheDocument()
```

### 2. A context

You generally need some kind of map `key: value` to match your mustache tags.
To create such map, use the `Ktm.ctx` object. The returned context will be used to
replace your document tags. With it, you can create contexts anywhere in your code
and call your mapper to create more complex context.

Since classes can't be used to create contexts manually, yet, you have to be
declarative. Even if it's a bit verbose, it gives you full control.

Let's assume this document:

```handlebars
{{# greeting }}
    Hello {{ name }},
{{/ greeting }}

Today tasks:
{{# tasks }}
    - {{ . }}
{{/ tasks }}
```

You can create a fully declarative context.

Every method in `Ktm.ctx` can be used in the `make scope`.

```kotlin
val context = Ktm.ctx.make { // make scope
    "greeting" by make { // make scope
        "name" by "Jon"
    }
    "tasks" by makeList { // make scope
        + "Sleep"
        + "Eat"
    }
}
```

If you already have some map or list (**Only String**) to fill the context, you can
use them:

```kotlin
val user = mapOf("name" to "Jon")
val tasks = listOf("Sleep", "Eat")

val context = Ktm.ctx.make {
    "greeting" by user
    "tasks" by tasks
}
```

Maybe you don't have the full scope of your context, and it needs to be dynamic.

Here we have a function that defines the tasks without knowing `mister`. We then add
it to the root context. When rendering, it will search for `mister` tag to create the
entry.

```kotlin
fun tasks() = Ktm.ctx.makeList {
    + "Call for lunch."
    + stringDelegate {
        "Welcome ${getValue("mister")} to the office."
    }
}

val context: MContext = Ktm.ctx.make {
    "name" by "Jon"
    "greeting" by true
    "mister" by "M. Smith"
    "tasks" by tasks()
}
```

### 3. Partials

You can create a pool of document that will be used as partials of a mustache
template with `Ktm.pool`.

Here is a quick example:

```kotlin
val base: MDocument = Ktm.doc.string("{{> header}}\n{{> body }}")

val pool = Ktm.pool.make {
    "base" by base
    "header" by string("Hello {{ name }},\n")
    "body" by "You need to tell {{ other }} of your accomplishment !"
}

val context = Ktm.ctx.make {
    "name" by "Jon"
    "other" by "Lola"
}

val render = base.render(context, pool)
val render = pool.render("base", context)
```

Both `render` are equivalent:

```
Hello Jon,
You need to tell Lola of your accomplishment !
```

As you can see, the `Ktm.pool.make` is like the `Ktm.ctx.make` scope. You can compose
your partials with the `by` keyword. Additionally, you can use all the methods from
the `Ktm.doc` to parse your documents in the `Ktm.pool.make` scope.

To render a document you can choose to render from an external document
with `document.render(context, partials)` or from the pool directly
with `pool.render("document_key", context)`.


> [!NOTE]
> Documents aren't cached. If you create two pools that read the same document.
> Like :
>
> ```kotlin
> val p1 = Ktm.pool.make { "a" by resource("/path") }
> val p2 = Ktm.pool.make { "a" by resource("/path") }
> ```
>
> The file will be in memory twice. To avoid it, create the document before:
>
> ```kotlin
> val a = Ktm.doc.resource("/path")
> val p1 = Ktm.pool.make { "a" by a }
> val p2 = Ktm.pool.make { "a" by a }
> ```

# Deep dive

- [Create documents](docs/mdocs/create_documents.md)
- **TODO** [Create contexts](docs/mdocs/create_contexts.md)
- **TODO** [Create partials](docs/mdocs/create_partials.md)
- **TODO** [Render to a stream](docs/mdocs/render_to_stream.md)
