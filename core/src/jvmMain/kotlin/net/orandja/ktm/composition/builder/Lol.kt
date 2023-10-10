package net.orandja.ktm.composition.builder

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.render

fun main() {
    val base: MDocument = Ktm.doc.string("{{> header}}\n{{> body }}")

    val pool = Ktm.pool.make {
        "base" by base
        "header" by string("Hello {{ name }},\n")
        "body" by "You need to tell {{ other }} of your accomplishment !"
    }

    val context = Ktm.ctx.make {
        "name" by "Jon"
        "other" by "Lola"
    }

    println(base.render(context, pool))
    println(pool.render("base", context))
}