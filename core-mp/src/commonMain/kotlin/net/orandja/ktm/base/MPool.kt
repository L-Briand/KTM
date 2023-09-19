package net.orandja.ktm.base

/**
 * Something that holds mustache documents.
 * Used for partial search during rendering.
 */
interface MPool {

    /** Should return associated document by [name] */
    operator fun get(name: String): MDocument?

    /** An empty pool when rendering a single document without partials. */
    object Empty : MPool {
        override fun get(name: String): MDocument? = null
    }
}
