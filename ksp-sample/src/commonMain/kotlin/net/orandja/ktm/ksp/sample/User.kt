package net.orandja.ktm.ksp.sample

import net.orandja.ktm.ksp.KtmContext
import net.orandja.ktm.ksp.KtmName

@KtmContext
data class User(val firstName: String, val lastName: String) {
    @KtmName("name")
    fun fullName() = "$firstName $lastName"
}