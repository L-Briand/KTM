package net.orandja.ktm.annotations.sample

import net.orandja.ktm.base.MContext
import net.orandja.ktm.annotations.KtmContext
import net.orandja.ktm.annotations.KtmIgnore
import net.orandja.ktm.annotations.KtmName


@KtmContext
class ClassWithInner {
    @KtmContext
    class Class
}

@KtmContext
data class ClassWithProperty(
    val foo: String = "foo",
    @KtmName("bar")
    var bah: String = "bar",
) {
    private var _count = 0

    val count get() = (_count++)
}

@KtmContext
class ClassCallable(
    @KtmIgnore
    var id: String
) {

    // check for dynamic
    fun function(): String = id
    val lambda: () -> String = { id }

    val lambdaNotTyped = { id }

    @KtmName("getIdFunction")
    fun toRenameFunction(): String = id

    @KtmName("getIdLambda")
    val toRenameLambda: () -> String = lambda

    fun paramContextFunction(context: MContext.Node): String = context.findValue(id).toString()
    val paramContextLambda: (MContext.Node) -> String = { it.findValue(id).toString() }

    fun MContext.Node.receiverContextFunction(): String = findValue(id).toString()
    val receiverContextLambda: MContext.Node.() -> String = { findValue(id).toString() }
}

@KtmContext
class Generic<T>(val data: T)