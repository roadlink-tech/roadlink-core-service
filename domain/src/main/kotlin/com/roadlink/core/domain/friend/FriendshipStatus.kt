package com.roadlink.core.domain.friend

enum class FriendshipStatus {
    FRIEND,
    NOT_FRIEND,
    PENDING_FRIENDSHIP_SOLICITUDE_SENT,
    PENDING_FRIENDSHIP_SOLICITUDE_RECEIVED,
    /**
     * This result indicates that the calculation of friendship status
     * involves the same user for both comparison roles
     */
    YOURSELF,
}