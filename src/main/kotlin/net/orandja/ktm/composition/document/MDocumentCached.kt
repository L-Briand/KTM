package net.orandja.ktm.composition.document

import net.orandja.ktm.base.MDocument

interface MDocumentCached : MDocument {
    fun reload()
}
