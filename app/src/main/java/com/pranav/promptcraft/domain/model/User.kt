package com.pranav.promptcraft.domain.model

/**
 * Domain model representing a user
 */
data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val isGuest: Boolean = false
)
