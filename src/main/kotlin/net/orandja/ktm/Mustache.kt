package net.orandja.ktm

import net.orandja.ktm.base.*
import net.orandja.ktm.composition.*

object Mustache {
    val parser = MustacheParser
    var renderer: MRender = MustacheRenderer
    val context = MustacheContext
    val document = MustacheDocument
    val pool = MustachePool
}

val emptyPool: MPool = MPool.Empty

fun MDocument.execute(context: MContext, pool: MPool = MPool.Empty, writer: (CharSequence) -> Unit) =
    Mustache.renderer.render(this, pool, context, writer)

fun MDocument.render(context: MContext, pool: MPool = MPool.Empty) =
    Mustache.renderer.renderToString(this, pool, context)

fun MPool.render(tag: String, context: MContext) =
    get(tag)?.render(context, this)

fun MPool.render(tag: String, context: MContext, writer: (CharSequence) -> Unit) =
    get(tag)?.execute(context, this, writer)
