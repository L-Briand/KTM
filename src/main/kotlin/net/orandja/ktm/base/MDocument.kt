package net.orandja.ktm.base

/**
 * A document is a simple interface holding :
 *
 * - A [MProvider] which should give parts of a template.
 * - A [MToken.Section] which correspond to the root token of the template.
 */
interface MDocument {
    val provider: MProvider
    val tokens: MToken.Section
}