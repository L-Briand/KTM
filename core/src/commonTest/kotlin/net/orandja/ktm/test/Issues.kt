package net.orandja.ktm.test

import net.orandja.ktm.render
import kotlin.test.Test
import kotlin.test.assertEquals

class Issues {

    /**
     * Single line containing part of delimiter like `'{'` is considered WHITE_SPACE.
     */
    @Test
    fun issue_2() {
        // @formatter:off
        val template = "{\n{ \n {\n { \n}\n} \n }\n } \n" +
                "{{ tag }}\n" +
                "{{= 123 456 =}}\n" +
                "12\n12 \n 12\n 12 \n45\n45 \n 45\n 45 \n" +
                "1\n1 \n 1\n 1 \n4\n4 \n 4\n 4 \n" +
                " 123 tag 456\n" +
                "123= @@@ @@@ =456\n" +
                "@@\n@@ \n @@\n @@ \n@@\n@@ \n @@\n @@ \n" +
                "@\n@ \n @\n @ \n@\n@ \n @\n @ \n" +
                " @@@ tag @@@ \n" +
                "@@@\n tag \n@@@\n" +
                "end"

        val render = template.render(mapOf("tag" to "value"))

        val expected = "{\n{ \n {\n { \n}\n} \n }\n } \n" +
                "value\n" +
                "12\n12 \n 12\n 12 \n45\n45 \n 45\n 45 \n" +
                "1\n1 \n 1\n 1 \n4\n4 \n 4\n 4 \n" +
                " value\n" +
                "@@\n@@ \n @@\n @@ \n@@\n@@ \n @@\n @@ \n" +
                "@\n@ \n @\n @ \n@\n@ \n @\n @ \n" +
                " value \n" +
                "value\n" +
                "end"
        // @formatter:on
        assertEquals(expected, render)
    }
}