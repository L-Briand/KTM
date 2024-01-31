# KTM

A [Mustache](https://mustache.github.io) implementation in pure Kotlin Multiplatform.

To see how it compares to [Mustache.java](https://github.com/spullara/mustache.java)
look [here](benchmark/README.MD)

# Import from maven

### Library

<details> 
<summary>Multiplatform:</summary>

```kotlin
repositories {
    mavenCentral()
}
val commonMain by getting {
    dependencies {
        implementation("net.orandja.ktm:core:1.0.0")
    }
}
```

</details>

Jvm:

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("net.orandja.ktm:core:1.0.0")
}
```

### Ksp code generator plugin

Enable ksp plugin:

```kotlin
plugins {
    // ...
    id("com.google.devtools.ksp") version "1.9.22-1.0.7" // or later
}
```

<details> 
<summary>Multiplatform:</summary>

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    add("kspJvm", "net.orandja.ktm:ksp:0.0.1")
    // add("kspJs", "net.orandja.ktm:ksp:0.0.1")
    // add("kspNative", "net.orandja.ktm:ksp:0.0.1")
    // ...
}
```

</details>

Jvm :

```kotlin
dependencies {
    // ...
    ksp("net.orandja.ktm:ksp:0.0.1")
}

```

> [!NOTE]
> If you provide ksp arguments.
>
> ```kotlin
> ksp {
>     arg("ktm.auto_adapters_package", "com.example")
> }
> ```
>
> This will generate the `com.example.AutoKtmAdaptersModule` with all generated
> adapters from `@KtmContext` annotated classes.
>
> Then set it as default with
> ```kotlin 
> Ktm.setDefaultAdapters(AutoKtmAdaptersModule)
> ```

# Examples

Render a document with automatically generated adapter

```kotlin
@KtmContext
data class User(val firstName: String, val lastName: String) {
    @KtmName("name")
    fun fullName() = "$firstName $lastName"
}

Ktm.setDefaultAdapters {
    +UserKtmAdapter
}

// or Ktm.setDefaultAdapters(AutoKtmAdaptersModule) 
// if you have setup a package name for it

val document = "Hello {{ name }}".toMustacheDocument()
val data = User("John", "Doe")

assert("Hello John Doe" == document.render(data))
```

Render a document with custom build contexts

```kotlin
val document = "Hello {{ name }}".toMustacheDocument()
val context = Ktm.ctx.make {
    "firstName" by "John"
    "lastName" by "Doe"
    "name" by delegateValue { "${findValue("firstName")} ${findValue("lastName")}" }
}
assert("Hello John Doe" == document.render(data))
```

Custom context can be useful when composing.

```kotlin
@KtmContext
data class User(val name: String)
Ktm.setDefaultAdapters { +UserKtmAdapter }

val john = User("John")

val documents = Ktm.ctx.make {
    "content" by "Hello {{ name }}".toMustacheDocument()
    // If you do not transform it to a mustache document,
    // it will be done on the fly when rendering
    "header" by "Header for {{ name }}"
}

val template = "{{> header }}\n\n{{> content }}".toMustacheDocument()

val context = Ktm.ctx.make {
    like(documents)
    like(john)
}

assert("Header for John\nHello John" == template.render(context))
```

# QuickStart

Mustache API is bundled into the `Ktm` object.
You will use it when you create documents, contexts, or use adapters.

To render a document, you need two things:

## 1. A template

Something like `Hello {{ name }}`. This needs to be parsed to be used.

Use the `Ktm.doc` object to read and parse the documents into usable objects.
If you are running kotlin with a JVM, you can use extension functions to parse files
and IO with `.file(File)`, `.path(Path)`, `.resource(name: String)`,
`.inputStream(InputStream)` or `.reader(Reader)`.

Example:

```kotlin
val mustacheTemplate: String = "Hello {{ name }}"
var document: MDocument
// default way
document = Ktm.doc.string(mustacheTemplate)
// quick way, only work on strings
document = mustacheTemplate.toMustacheDocument()
```

## 2. A context

You generally need some kind of map `key: value` to match your mustache tags.
To create such map, use the `Ktm.ctx` object.
With it, you can create template contexts anywhere in your code.

Let's assume this document:

```handlebars
{{# greeting }}
Hello {{ name }},
{{/ greeting }}
Today's tasks:
{{# tasks }}
    - {{ . }}
{{/ tasks }}
```

### Creating context by hand

You can build a context by hand with methods in `Ktm.ctx`.

```kotlin
val context = Ktm.ctx.ctxMap(
    "greeting" to Ktm.ctx.ctxMap(
        "name" to Ktm.ctx.string("John")
    )
)
```

This method is quite cumbersome, to mitigate this, and write things more quickly you
can use the `make` function.

The `by` keyword can be used to associate key value pairs in the context.
Also, every method in `Ktm.ctx` can be used in the `make` scope.

```kotlin
val context = Ktm.ctx.make {
    "greeting" by make {
        "name" by "Jon"
    }
    "tasks" by ctxList(value("Eat"), value("Work"))
}
```

In a more simple way, you can also use kotlin's maps and list to define your data

```kotlin
val context = Ktm.ctx.make {
    "greeting" by mapOf("name" to "Jon")
    "tasks" by listOf("Eat", "Work")
}
```

Maybe you don't have the full scope of your context, and some parts need to be
dynamic.

Here we have a function that defines the tasks without knowing `name`.
We then add it to the main context.
During render, it will call the lambda and search for the `name` tag.

```kotlin
val tasks = Ktm.ctx.makeList {
    +"Call for lunch."
    +delegateValue {
        "Welcome ${findValue("name")} to the office."
    }
}

val context: MContext = Ktm.ctx.make {
    "name" by "John"
    "tasks" by tasks
}
```

### Creating context with ksp

Create a class representing your data and annotate it with `@KtmContext`.

```kotlin
@KtmContext
data class User(@KtmName("name") val user: String)
```

The ksp plugin will take these classes and create
adapters (`UserKtmAdapter`, `TasksKtmAdapter`) in the same package.
It will create bindings for any property or function inside it.

Adapters are key components for Ktm. From a given type (here `User`) it has the
ability convert it into `MContext`.

```kotlin
val context: MContext = TasksKtmAdapter.toMustacheContext(User("John"))
assert("John" == "{{ name }}".render(context))
```

Generally, you want to set up sets of adapters to transform all your objects
into context easily. Then use it when you build contexts.

```kotlin
val adapters = Ktm.adapters.make {
    +UserKtmAdapter
}
val context = Ktm.ctx.make(adapters) {
    "content" by User("John")
}
assert("John", "{{ content.name }}".render(context))
```

Or set a new default set of adapters altogether for every time you create a context.

```kotlin
Ktm.setDefaultAdapters {
    +UserKtmAdapter
}
// now 'Ktm.adapters' contains adapter for User.
// and by default 'Ktm.ctx.make' use 'Ktm.adapters'
val userContext = Ktm.adapters.contextOf(User("John"))
val contentContext = Ktm.ctx.make {
    "content" by User("John")
}
assert("John" == "{{ name }}".render(userContext))
assert("John" == "{{ content.name }}".render(userContext))
```

You can always add your own adapter on top of others.

```kotlin
@KtmContext
data class User(val user: String)
Ktm.setDefaultAdapters { +UserKtmAdapter }

val customAdapters = Ktm.adapters.make {
    +KtmAdapter<User> { adapters, value ->
        Ktm.ctx.make {
            "userName" by value.user
        }
    }
}
val context = customAdapters.contextOf(User("John"))
assert("John", "{{userName}}".render())
```

#### KtmAdapter Modules

If you set the `ksp` argument:

```kotlin
ksp {
    arg("ktm.auto_adapters_package", "my.pkg")
}
```

This will generate the `my.pkg.AutoKtmAdaptersModule` with all generated
adapters from `@KtmContext` annotated classes. You can then set it as default with:

```kotlin 
Ktm.setDefaultAdapters(AutoKtmAdaptersModule)
```

Extends the class `KtmAdapterModule` to create custom modules. You can later create
sets of adapters with them.

# Deep dive

- [Create documents](docs/mdocs/create_documents.md)
