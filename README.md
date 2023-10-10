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

# Usage

With Mustache, to render a document you need three things:

### 1. A template

> [learn more about creating documents](create_documents.md).

Something like `Hello {{ name }}`. This needs to be parsed to be used. Use
the `Ktm.doc` object to read and parse the documents into a usable object. If you are
running kotlin JVM, you can use extension functions to parse files and IO.

```kotlin
val mustacheTemplate: String = "Hello {{ name }}"
var document: MDocument
document = Ktm.doc.string(mustacheTemplate)
```

### 2. A context

> [learn more about creating contexts](create_contexts.md)

To create a context, use `Ktm.ctx`. It will be used when rendering a document in
place of mustache tags. You generally need some kind of map `key: value` to match
your mustache tags. This is done with the keyword `by` in a `ContextBuilder` scope.

Since you cannot use classes as context, everything is declarative. Here is an
example to create a tasks list:

```kotlin
val document = Ktm.doc.string(
    """
{{ date }} tasks:
{{# tasks }}
- {{ name }}: {{ description }}  
{{/ tasks }}
"""
)

fun taskItem(name: String, description: String): MContext = Ktm.ctx.make {
    "name" by name
    "description" by description
}

val context: MContext = Ktm.ctx.make {
    "date" by "Today"
    "tasks" by list(
        make {
            "name" by "Sleep"
            "description" by "Get at least 8h of sleep at work"
        },
        taskItem("Work", "Produce some code"),
    )
}

println(document.render(context))
```

This produce:

```
Today tasks:
- Work: Produce some code  
- Sleep: Get at least 8h of sleep at work  
```

Inside the `Ktm.ctx.make` scope, you can use all the function of the `Ktm.ctx`. It
allows making contexts more easily. As shown in the example, you can create contexts
anywhere in your code and call it to
create more complex context.

### 3. Partials

> [learn more about using partials](create_partials.md)

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

Both `render` the same:

```
Hello Jon,
You need to tell Lola of your accomplishment !
```

As you can see, the `Ktm.pool.make` is like the `Ktm.ctx.make` scope. You can compose
your partials with the `by` keyword. Additionally, you can use all the methods from
the `Ktm.doc` to parse your documents in the `Ktm.pool.make` scope.

To render a document can choose to render from an external document
with `document.render(<context>, <partials>)` or from the pool directly
with `pool.render("<document_key>", <context>)`. 

## Limitations

### No classes as context

You cannot use classes as context to render a document yet. This means you would
not be able to use it as a drop-in replacement of another mustache lib. You need to
create all your contexts manually.

This is **not yet possible**:

```kotlin
data class User(val name: String, val age: Int)

val document = Ktm.doc.string("Hello {{ name }} ! Happy {{ age }} birthday !")
val context = User("Jon", 33)
document.renderToString(context)
```

# Import into your project

> TODO: Write a better section when publication is done.

For now, you can clone this repo build and publish locally.

`./gradlew build` && `./gradlew publishToMavenLocal`

Then import it:

```kotlin
repositories {
    mavenLocal()
}
dependencies {
    implementation("net.orandja.ktm:core:0.1.0")
}
```
