package net.orandja.ktm.ksp.sample

import net.orandja.ktm.ksp.KtmContext


@KtmContext
enum class EnumWithInner {
    ;

    @KtmContext
    enum class Enum
}

@KtmContext
enum class EnumWithProperty {
    FOO, BAR, BAZ;
}