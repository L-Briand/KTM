package net.orandja.ktm.ksp.sample

import net.orandja.ktm.base.NodeContext
import net.orandja.ktm.ksp.KtmContext
import net.orandja.ktm.ksp.KtmDynamic
import net.orandja.ktm.ksp.KtmIgnore
import net.orandja.ktm.ksp.KtmName


@KtmContext
class ClassWithInner {
    @KtmContext
    class Class
}

@KtmContext
data class ClassWithProperty(
    val foo: String = "foo",
    @KtmName("bar")
    val bah: String = "bar",
) {
    private var _count = 0

    @KtmDynamic
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

    fun paramContextFunction(context: NodeContext): String = context.findValue(id).toString()
    val paramContextLambda: (NodeContext) -> String = { it.findValue(id).toString() }

    fun NodeContext.receiverContextFunction(): String = findValue(id).toString()
    val receiverContextLambda: NodeContext.() -> String = { findValue(id).toString() }
}

@KtmContext
class Generic<T>(val data: T)