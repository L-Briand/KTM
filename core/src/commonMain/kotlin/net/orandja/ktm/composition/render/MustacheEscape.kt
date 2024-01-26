package net.orandja.ktm.composition.render

object MustacheEscape {
    const val AMP = '&'
    const val LT = '<'
    const val GT = '>'
    const val D_QUOT = '"'
    const val S_QUOT = '\''
    const val B_QUOT = '`'
    const val EQUAL = '='

    const val AMP_REPLACE = "&amp;"
    const val LT_REPLACE = "&lt;"
    const val GT_REPLACE = "&gt;"
    const val D_QUOT_REPLACE = "&quot;"
    const val S_QUOT_REPLACE = "&#x27;"
    const val B_QUOT_REPLACE = "&#x60;"
    const val EQUAL_REPLACE = "&#x3D;"

    fun escape(cs: CharSequence, writer: (CharSequence) -> Unit) {
        var start = 0
        var idx = 0
        while (idx < cs.length) {
            when (cs[idx]) {
                AMP -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(AMP_REPLACE)
                    start = idx + 1
                }

                LT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(LT_REPLACE)
                    start = idx + 1
                }

                GT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(GT_REPLACE)
                    start = idx + 1
                }

                D_QUOT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(D_QUOT_REPLACE)
                    start = idx + 1
                }

                S_QUOT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(S_QUOT_REPLACE)
                    start = idx + 1
                }

                B_QUOT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(B_QUOT_REPLACE)
                    start = idx + 1
                }

                EQUAL -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(EQUAL_REPLACE)
                    start = idx + 1
                }
            }
            idx++
        }
        if (start < idx) writer(cs.subSequence(start, idx))
    }

}