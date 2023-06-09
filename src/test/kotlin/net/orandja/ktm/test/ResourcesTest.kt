package net.orandja.ktm.test

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.orandja.ktm.Mustache
import net.orandja.ktm.render
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class ResourcesTest {

    @Test
    fun comments() = execute(loadResource("comments.json"))

    @Test
    fun delimiters() = execute(loadResource("delimiters.json"))

    @Test
    fun interpolation() = execute(loadResource("interpolation.json"))

    @Test
    fun inverted() = execute(loadResource("inverted.json"))

    @Test
    fun partials() = execute(loadResource("partials.json"))

    @Test
    fun sections() = execute(loadResource("sections.json"))

    private val codec = Json {
        ignoreUnknownKeys = true
    }

    fun loadResource(file: String): JsonResource {
        val raw = ResourcesTest::class.java.getResource("/$file")!!.openStream().reader().use { it.readText() }
        return codec.decodeFromString(serializer(), raw)
    }

    private fun execute(resource: JsonResource) {
        val tests = resource.tests.map { JsonTest(it) }
        Assertions.assertAll(tests)
    }

    class JsonTest(private val test: JsonResource.Test) : Executable {
        override fun execute() {
            val context = jsonToContext(test.data)
            val template = Mustache.document.string(test.template)
            val partials = test.partials?.let { partials ->
                Mustache.pool.delegateCached { name ->
                    partials[name]?.let(::string)
                }
            } ?: Mustache.pool.empty
            val rendered = template.render(context, partials)
            val a = test.expected.trim().split("\\s+".toRegex())
            val b = rendered.trim().split("\\s+".toRegex())
            Assertions.assertEquals(a, b) { "\n'${test.name}' :\n" }
        }
    }
}
