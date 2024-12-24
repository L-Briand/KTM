@file:OptIn(ExperimentalJsExport::class)

import kotlinx.browser.document
import net.orandja.ktm.Ktm
import net.orandja.ktm.render
import kotlin.properties.Delegates

// public part

@JsExport
fun setContentId(id: String) {
    contentId = id
    display(counter)
}

@JsExport
fun increment() = counter++

@JsExport
fun decrement() = counter--

// private part

private var contentId = ""
private var counter by Delegates.observable(0) { _, _, count ->
    display(count)
}

private val template = "The new count is <b>{{count}}</b>"
private fun display(count: Int) {
    if (contentId.isBlank()) return
    val element = document.getElementById(contentId) ?: return
    element.innerHTML = template.render(mapOf("count" to count))
}
