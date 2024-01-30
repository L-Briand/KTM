package net.orandja.ktm.benchmark

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.contextOf
import net.orandja.ktm.render
import net.orandja.ktm.toMustacheDocument

object LBriandKtm {
    fun compile(template: String): MDocument = template.toMustacheDocument()

    inline fun <reified T : Any> render(template: MDocument, context: T) =
        render(template, Ktm.adapters.contextOf<T>(context))

    fun render(template: MDocument, context: MContext): String = template.render(context)
}