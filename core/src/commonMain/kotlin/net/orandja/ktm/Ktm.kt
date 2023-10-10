package net.orandja.ktm

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.builder.*
import net.orandja.ktm.composition.parser.Parser
import net.orandja.ktm.composition.render.Renderer

object Ktm {
    val parser = Parser()
    val renderer = Renderer()
    val ctx = ContextFactory()
    val doc = DocumentBuilder(parser)
    val pool = PoolFactory(parser)
}
fun MDocument.render(
    context: MContext,
    pool: MPool = MPool.Empty,
    writer: (CharSequence) -> Unit,
) = Ktm.renderer.render(this, context, pool, writer)

fun MDocument.render(
    context: MContext,
    pool: MPool = MPool.Empty,
) = Ktm.renderer.renderToString(this, context, pool)

fun MPool.render(
    name: String,
    context: MContext,
) = get(name)?.render(context, this)

fun MPool.render(
    tag: String,
    context: MContext,
    writer: (CharSequence) -> Unit,
) = get(tag)?.render(context, this, writer)
