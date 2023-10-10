package net.orandja.ktm.test

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.builder.file
import net.orandja.ktm.composition.builder.path
import java.io.File
import kotlin.test.Test


class ExtendedDocBuilderTest {
    val reference: MDocument = Ktm.doc.string(ExtendedDocBuilderTest::class.java.getResource("/document.mustache")!!.readText())
    val documentFile = File("src/jvmTest/resources/document.mustache")
    @Test
    fun testFile() {
        val document = Ktm.doc.file(documentFile)
        assert(document == reference)
    }

    @Test
    fun testPath() {
        val document = Ktm.doc.path(documentFile.toPath())
        assert(document == reference)
    }
}