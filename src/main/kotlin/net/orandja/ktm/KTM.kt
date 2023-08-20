package net.orandja.ktm

import net.orandja.ktm.base.*
import net.orandja.ktm.composition.*

object KTM {
    var renderer: MRender = MustacheRenderer
    val context = MustacheContext
    val document = MustacheDocument
    val pool = MustachePool
}

fun MDocument.execute(context: MContext, pool: MPool = KTM.pool.empty, writer: (CharSequence) -> Unit) =
    KTM.renderer.render(this, pool, context, writer)

fun MDocument.render(context: MContext, pool: MPool = MPool.Empty) =
    KTM.renderer.renderToString(this, pool, context)

fun MPool.render(tag: String, context: MContext) =
    get(tag)?.render(context, this)

fun MPool.render(tag: String, context: MContext, writer: (CharSequence) -> Unit) =
    get(tag)?.execute(context, this, writer)
