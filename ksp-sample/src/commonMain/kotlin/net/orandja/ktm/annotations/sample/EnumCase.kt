package net.orandja.ktm.annotations.sample

import net.orandja.ktm.annotations.KtmContext


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