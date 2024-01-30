package net.orandja.ktm.test.spec

import kotlinx.serialization.json.Json
import net.orandja.ktm.Ktm
import net.orandja.ktm.render
import kotlin.test.Test
import kotlin.test.assertEquals

class SpecificationTest {

    companion object {
        private val codec = Json {
            ignoreUnknownKeys = true
        }

        private fun decodeResource(jsonString: String): TestResource =
            codec.decodeFromString(TestResource.serializer(), jsonString)
    }


    @Test
    fun comments() = execute(resourceTestComments)

    @Test
    fun delimiters() = execute(resourceTestDelimiters)

    @Test
    fun interpolation() = execute(resourceTestInterpolation)

    @Test
    fun inverted() = execute(resourceTestInverted)

    @Test
    fun partials() = execute(resourceTestPartials)

    @Test
    fun sections() = execute(resourceTestSections)


    private fun execute(jsonString: String) {
        val rawTests = decodeResource(jsonString)
        val tests = rawTests.tests.map { JsonTest(it) }
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
