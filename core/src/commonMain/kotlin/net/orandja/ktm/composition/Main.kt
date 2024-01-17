package net.orandja.ktm.composition

import net.orandja.ktm.composition.builder.StringCharStream
import net.orandja.ktm.composition.parser.Parser
import net.orandja.ktm.composition.parser.TokenParser
import net.orandja.ktm.composition.parser.TokenParserContext

fun main() {
    val template = """
        Empty line
        
        White Content
        
           
        
        Normal {{ tag }}
        Escaped {{& tag }} 
        Escaped {{{ tag }}}
        Comment {{! comment }}
        Partial1 {{> partial }}
        Partial2
            {{> partial }}
        Section {{# section }} content {{/ section }}
        
    """.trimIndent()
    val context = TokenParserContext(StringCharStream(template))
    TokenParser.parse(context).joinToString { it.toString() }
    val document = Parser.parse(StringCharStream(template))
}

