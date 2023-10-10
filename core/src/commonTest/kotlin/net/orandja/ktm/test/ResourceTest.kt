package net.orandja.ktm.test

import net.orandja.ktm.Ktm
import net.orandja.ktm.render
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceTest {

    @Test
    fun comments() = execute(ResourceLoader.load("comments.json"))

    @Test
    fun delimiters() = execute(ResourceLoader.load("delimiters.json"))

    @Test
    fun interpolation() = execute(ResourceLoader.load("interpolation.json"))

    @Test
    fun inverted() = execute(ResourceLoader.load("inverted.json"))

    @Test
    fun partials() = execute(ResourceLoader.load("partials.json"))

    @Test
    fun sections() = execute(ResourceLoader.load("sections.json"))


    private fun execute(resource: TestResource) {
        val tests = resource.tests.map { JsonTest(it) }
        for (test in tests) test.execute()
    }

    class JsonTest(private val test: TestResource.Test) {
        fun execute() {
            val context = jsonToContext(test.data)
            val template = Ktm.doc.string(test.template)
            val partials = test.partials?.let { partials ->
                Ktm.pool.delegate { name ->
                    partials[name]?.let(::string)
                }
            } ?: Ktm.pool.empty
            val rendered = template.render(context, partials)
            assertEquals(test.expected, rendered, "\n## ${test.name}:\n")
        }
    }
}
