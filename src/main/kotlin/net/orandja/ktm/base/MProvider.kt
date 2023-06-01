package net.orandja.ktm.base

/**
 * A provider should give parts of a template when a MDocument is render.
 * When a [MContext.Multi] is rendered, the same part of a document can be read multiple times.
 */
// TODO : Allow streamed resources to cache parts of documents
// I don't have such use case for now.. To fully allow stream like sources as MProvider (like urls).
// The renderer should say something like "this part of the document will be redrawn multiple times" to the MProvider
// MToken.Section already contains this information (Section.toRender).
interface MProvider {

    /** Should return the given part of the resource. */
    fun subSequence(from: Long, to: Long): CharSequence
    fun subSequence(range: LongRange) = subSequence(range.first, range.last + 1)

    /**
     * The provider might have open a resource to give parts of it.
     * The renderer call this to close it.
     */
    fun close()
}
