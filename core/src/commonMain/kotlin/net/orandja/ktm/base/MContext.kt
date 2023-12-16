package net.orandja.ktm.base

import net.orandja.ktm.composition.NodeContext

/**
 * Contextual part a mustache document.
 * Combine with [MDocument], you can render a document.
 */
sealed interface MContext {

    /**
     * A contextual element used to render:
     * - A section that should not render.
     * - An inverted sections.
     * - Nothing in a tag.
     */
    data object No : MContext

    /**
     * A contextual element used to render a section without tags.
     *
     * Example:
     * ```handlebars
     * {{# section }} section without tags inside {{/ section }}
     * ```
     */
    data object Yes : MContext

    /**
     * A contextual element used to render a single tag.
     *
     * Example:
     * ```handlebars
     * Hello {{ name }}
     * ```
     */
    fun interface Value : MContext {
        fun get(node: NodeContext): CharSequence
    }

    /**
     * A contextual element used to render a Section with a specific tags inside.
     *
     * Example:
     * ```handlebars
     * {{#greeting}} Hello {{Jon}} {{/greeting}}
     * ```
     */
    fun interface Map : MContext {
        fun get(node: NodeContext, tag: String): MContext?
    }

    /**
     * A contextual element used to render a Section multiple times.
     *
     * Example
     * ```handlebars
     * <ul>
     *   {{# element }} <li> {{name}} </li> {{/element}}
     * </ul>
     * ```
     */
    fun interface List : MContext {
        fun iterator(node: NodeContext): Iterator<MContext>
    }

    /**
     * Special Contextual element.
     * This is used to create contexts on the fly.
     * This element should return any other element.
     */
    fun interface Delegate : MContext {
        fun get(node: NodeContext): MContext
    }
}
