package net.orandja.ktm

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.base.MRenderer
import net.orandja.ktm.composition.builder.ContextBuilder
import net.orandja.ktm.composition.builder.PoolBuilder
import net.orandja.ktm.composition.render.DefaultRenderer
import net.orandja.ktm.composition.render.FastRenderer
import java.io.StringWriter

object KTM {
    val defaultRenderer = DefaultRenderer()
    val fastRenderer = FastRenderer()
    val ctx = ContextBuilder(null)
    val doc = PoolBuilder()
}

fun MDocument.render(
    context: MContext,
    pool: MPool = MPool.Empty,
    renderer: MRenderer = KTM.defaultRenderer,
    writer: (CharSequence) -> Unit,
) = renderer.render(this, pool, context, writer)

fun MDocument.render(
    context: MContext,
    pool: MPool = MPool.Empty,
    renderer: MRenderer = KTM.defaultRenderer,
) = renderer.renderToString(this, pool, context)

fun MPool.render(
    name: String,
    context: MContext,
    renderer: MRenderer = KTM.defaultRenderer,
) = get(name)?.render(context, this, renderer)

fun MPool.render(
    tag: String,
    context: MContext,
    renderer: MRenderer = KTM.defaultRenderer,
    writer: (CharSequence) -> Unit,
) = get(tag)?.render(context, this, renderer, writer)

fun main() {
    val bench = Bench()
    val writer = StringWriter()
    bench.mustache.execute(writer, bench.mfContext)
    println(bench.mustache)
    println(writer.toString())
    println(bench.doc1.render(bench.context))
}