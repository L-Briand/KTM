package net.orandja.ktm.test

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.orandja.ktm.KTM
import net.orandja.ktm.render
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class ResourceTest {

    @Test
    fun test() {
        val document = KTM.doc.string("{{ hello.world }}")
        val context = KTM.ctx.make {
            "hello.world" by "multitag"
        }
        Assertions.assertEquals("multitag", document.render(context))
    }

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
        val raw = ResourceTest::class.java.getResource("/$file")!!.openStream().reader().use { it.readText() }
        return codec.decodeFromString(serializer(), raw)
    }

    private fun execute(resource: JsonResource) {
        val tests = resource.tests.map { JsonTest(it) }
        Assertions.assertAll(tests)
    }

    class JsonTest(val test: JsonResource.Test) : Executable {
        override fun execute() {
            val context = jsonToContext(test.data)
            val template = KTM.doc.string(test.template)
            val partials = test.partials?.let { partials ->
                KTM.doc.delegate { name ->
                    partials[name]?.let(::string)
                }
            } ?: KTM.doc.empty
            val rendered = template.render(context, partials)
            Assertions.assertEquals(test.expected, rendered) { "\n## ${test.name}:\n" }
        }
    }
}
