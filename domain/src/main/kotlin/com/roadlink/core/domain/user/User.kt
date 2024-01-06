package com.roadlink.core.domain.user

import java.util.*

/* TODO:
    1- photo url
    2- genero
    3- edad
    4- cantidad de amigos devolverlo
* */
data class User(
    val id: UUID,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val creationDate: Date = Date(),
    internal val friends: MutableSet<UUID> = mutableSetOf()
) {

    fun beFriends(user: User) {
        if (this.id != user.id && !this.friends.contains(user.id)) {
            this.friends.add(user.id)
            user.beFriends(this)
        }
    }
}