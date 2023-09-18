package net.orandja.ktm

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.base.MRenderer
import net.orandja.ktm.composition.builder.ContextBuilder
import net.orandja.ktm.composition.builder.PoolBuilder
import net.orandja.ktm.composition.render.Renderer

object KTM {
    val renderer = Renderer()
    val ctx = ContextBuilder(null)
    val doc = PoolBuilder()
}

fun MDocument.render(
    context: MContext,
    pool: MPool = MPool.Empty,
    renderer: MRenderer = KTM.renderer,
    writer: (CharSequence) -> Unit,
) = renderer.render(this, context, pool, writer)

fun MDocument.render(
    context: MContext,
    pool: MPool = MPool.Empty,
    renderer: MRenderer = KTM.renderer,
) = renderer.renderToString(this, context, pool)

fun MPool.render(
    name: String,
    context: MContext,
    renderer: MRenderer = KTM.renderer,
) = get(name)?.render(context, this, renderer)

fun MPool.render(
    tag: String,
    context: MContext,
    renderer: MRenderer = KTM.renderer,
    writer: (CharSequence) -> Unit,
) = get(tag)?.render(context, this, renderer, writer)