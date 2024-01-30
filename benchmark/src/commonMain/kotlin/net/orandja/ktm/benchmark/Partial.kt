package net.orandja.ktm.benchmark

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.ksp.KtmContext

@KtmContext
data class Partial<T>(
    val title: String,
    val content: T,
) {
    companion object {
        val TEMPLATE = """
            <!doctype html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport"
                      content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
                <meta http-equiv="X-UA-Compatible" content="ie=edge">
                <title>{{title}}</title>
            </head>
            <body>
            {{#content}}{{>innerHtml}}{{/content}}  
            </body>
            </html>
        """.trimIndent()

        fun pool(innerHtml: MDocument) = Ktm.pool.make {
            "innerHtml" by innerHtml
        }
    }
}


