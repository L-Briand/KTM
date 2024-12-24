package net.orandja.ktm.annotations.sample

import net.orandja.ktm.annotations.KtmContext
import kotlin.jvm.JvmInline

@JvmInline
@KtmContext
value class ValueClass(val foo: String)