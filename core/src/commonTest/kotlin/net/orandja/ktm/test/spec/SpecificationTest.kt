package net.orandja.ktm.test.spec

import kotlinx.serialization.json.Json
import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
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
            try {
                val dataContext = jsonToContext(test.data)
                val partialsContext = MContext.Map { _, tag ->
                    Ktm.ctx.document(test.partials?.get(tag))
                }

                val context =
                    if (dataContext is MContext.Map) Ktm.ctx.merge(dataContext, partialsContext)
                    else dataContext

                val template = Ktm.doc.string(test.template)
                val rendered = template.render(context)
                assertEquals(test.expected, rendered, "\n## ${test.name}:\n")
            } catch (e: Exception) {
                throw IllegalStateException("Failed to execute ${test.name}", e)
            }
        }
    }
}
