package net.orandja.ktm.test

import net.orandja.ktm.render
import kotlin.test.Test
import kotlin.test.assertEquals

class Issues {

    /**
     * Single line containing part of start delimiter like `{` is considered WHITE_SPACE.
     */
    @Test
    fun issue_2() {
        val template = """
        {
          {{{content}}}
        }
        {{= 123 456 =}}
        12
          123{content}456
        45
        end
        """.trimIndent()
        val render = template.render(mapOf("content" to "staticValue"))
        val expected = """
        {
          staticValue
        }
        12
          staticValue
        45
        end
        """.trimIndent()
        assertEquals(expected, render)
    }
}