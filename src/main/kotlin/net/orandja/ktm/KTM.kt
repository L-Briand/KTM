package net.orandja.ktm

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.MustacheRenderer
import net.orandja.ktm.composition.builder.ContextBuilder
import net.orandja.ktm.composition.builder.PoolBuilder

object KTM {
    val ctx = ContextBuilder(null)
    val doc = PoolBuilder()
}

fun MDocument.render(context: MContext, pool: MPool = KTM.doc.empty, writer: (CharSequence) -> Unit) =
    MustacheRenderer.render(this, pool, context, writer)

fun MDocument.render(context: MContext, pool: MPool = MPool.Empty) =
    MustacheRenderer.renderToString(this, pool, context)

fun MPool.render(tag: String, context: MContext) =
    get(tag)?.render(context, this)

fun MPool.render(tag: String, context: MContext, writer: (CharSequence) -> Unit) =
    get(tag)?.render(context, this, writer)
