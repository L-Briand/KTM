package net.orandja.ktm.base

/**
 * Something used to render a mustache document.
 */
sealed interface MContext {

    /** For section that should not render. To print inverted sections. To print nothing */
    data object No : MContext

    /**
     * To render a section with current context.
     * `{{# section }} section without tags {{/ section }}`
     */
    data object Yes : MContext

    /** To render a tag. */
    fun interface Value : MContext {
        fun get(node: NodeContext): CharSequence
    }

    /** To render a section with a specific group. */
    fun interface Group : MContext {
        fun get(node: NodeContext, tag: String): MContext?
    }

    /** To render a section multiple times. Like a section list */
    fun interface Multi : MContext {
        fun iterator(node: NodeContext): Iterator<MContext>
    }
}
