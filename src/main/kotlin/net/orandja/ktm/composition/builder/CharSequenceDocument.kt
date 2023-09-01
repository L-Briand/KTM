package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MProvider
import net.orandja.ktm.base.MToken
import net.orandja.ktm.composition.MustacheParser

class CharSequenceDocument(val source: CharSequence) : MDocument {
    override val provider: MProvider = object : MProvider {
        override fun subSequence(from: Long, to: Long): CharSequence = source.subSequence(from.toInt(), to.toInt())
        override fun close() = Unit
    }
    override val tokens: MToken.Section = MustacheParser.parse(source)
}
