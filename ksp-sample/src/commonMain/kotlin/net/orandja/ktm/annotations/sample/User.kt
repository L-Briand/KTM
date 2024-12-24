package net.orandja.ktm.annotations.sample

import net.orandja.ktm.annotations.KtmContext
import net.orandja.ktm.annotations.KtmName

@KtmContext
data class User(val firstName: String, val lastName: String) {
    @KtmName("name")
    fun fullName() = "$firstName $lastName"
}