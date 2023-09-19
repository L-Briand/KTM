package net.orandja.ktm.test

import net.orandja.ktm.KTM
import net.orandja.ktm.render
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceTest {

    @Test
    fun comments() = execute(ResourceLoader.loadResource("comments.json"))

    @Test
    fun delimiters() = execute(ResourceLoader.loadResource("delimiters.json"))

    @Test
    fun interpolation() = execute(ResourceLoader.loadResource("interpolation.json"))

    @Test
    fun inverted() = execute(ResourceLoader.loadResource("inverted.json"))

    @Test
    fun partials() = execute(ResourceLoader.loadResource("partials.json"))

    @Test
    fun sections() = execute(ResourceLoader.loadResource("sections.json"))


    private fun execute(resource: JsonResource) {
        val tests = resource.tests.map { JsonTest(it) }
        for (test in tests) {
            test.execute()
        }
    }

    class JsonTest(val test: JsonResource.Test) {
        fun execute() {
            val context = jsonToContext(test.data)
            val template = KTM.doc.string(test.template)
            val partials = test.partials?.let { partials ->
                KTM.doc.delegate { name ->
                    partials[name]?.let(::string)
                }
            } ?: KTM.doc.empty
            val rendered = template.render(context, partials)
            assertEquals(test.expected, rendered, "\n## ${test.name}:\n")
        }
    }
}
