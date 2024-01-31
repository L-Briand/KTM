# Benchmark

Benchmarks are run in the JVM and compare time execution of:

- This lib `Ktm`
- [Mustache.java](https://github.com/spullara/mustache.java)

The test is to parse and render the same document.

1. Simple [[link](src/commonMain/kotlin/net/orandja/ktm/benchmark/Simple.kt)]:

```handlebars
<b>User:</b> {{ firstName }} {{ lastName }}
```

2. Complex [[link](src/commonMain/kotlin/net/orandja/ktm/benchmark/Complex.kt)]:

```handlebars
<b>Hello {{user.firstName}} {{user.lastName}}</b>
You have just won {{value}} dollars!</br>
{{#isTaxed}}
    Today's win is taxed at {{ rate }}%
{{/isTaxed}}
```

3. Multiple (x1, x10,
   x50) [[link](src/commonMain/kotlin/net/orandja/ktm/benchmark/Multiple.kt)]:

```handlebars
<h1>Last's winners</h1>
Total number of winners : {{ winnersCount }}
{{#winners}}
    <li>
        {{user.firstName}} {{user.lastName}} <br/>
        Amount : {{value}}{{#isTaxed}} at {{ rate }}%{{/isTaxed}}
    </li>
{{/winners}}
```

### run

- Smoke: `./gradlew :benchmark:jvmMainSmokeBenchmark`
- Full: `./gradlew :benchmark:jvmMainBenchmark`

### Results

Overall, performance is good:

- Parsing documents in `Ktm` is faster than `Mustache.java`.
- Rendering documents in `Ktm` takes longer than `Mustache.java` for Multiple render
  structures.

<details>
<summary>On my laptop:</summary>

``` 
OS: Kubuntu 23.10 x86_64
Kernel: 6.5.0-15-generic
CPU: 13th Gen Intel i9-13900H (20) @ 5.200GHz
openjdk version "17.0.9" 2023-10-17
```


[View benchmark](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/L-Briand/KTM/master/benchmark/benchmark/Laptop.json)

[raw json file](benchmark/Laptop.json)

</details>

<details>
<summary>On my mac mini:</summary>

``` 
Host: Mac mini 2023
CPU: M2
OS: macOS Sonoma Version 14.2.1
java version "21.0.1" 2023-10-17-LTS
```

[View benchmark](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/L-Briand/KTM/master/benchmark/benchmark/MacMini.json)

[raw json file](benchmark/MacMini.json)

</details>
