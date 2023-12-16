package net.orandja.ktm.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

class KtmLogger(
    private val delegate: KSPLogger,
    val tag: String = "KTM",
) : KSPLogger {
    fun copy(tag: String) = KtmLogger(delegate, tag)
    override fun logging(message: String, symbol: KSNode?) = delegate.logging("[$tag] $message", symbol)
    override fun info(message: String, symbol: KSNode?) = delegate.info("[$tag] $message", symbol)
    override fun warn(message: String, symbol: KSNode?) = delegate.warn("[$tag] $message", symbol)
    override fun error(message: String, symbol: KSNode?) = delegate.error("[$tag] $message", symbol)
    override fun exception(e: Throwable) = delegate.exception(e)
}