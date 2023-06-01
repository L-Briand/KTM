package net.orandja.ktm.base

/**
 * Something used to render a mustache document.
 */
sealed interface MContext {

    /** For section that should not render. To print inverted sections. To print nothing */
    object No : MContext {
        override fun toString(): String = "No"
    }

    /** To render a section with current context. */
    object Yes : MContext {
        override fun toString(): String = "Yes"
    }

    /** To render a tag. */
    fun interface Value : MContext {
        fun get(node: CtxNode): CharSequence
    }

    /** To render a section with a specific group. */
    fun interface Group : MContext {
        fun get(node: CtxNode, tag: String): MContext?
    }

    /** To render a section multiple times. Prints nothing when used as value. */
    fun interface Multi : MContext {
        fun iterator(node: CtxNode): Iterator<MContext>
    }
}
