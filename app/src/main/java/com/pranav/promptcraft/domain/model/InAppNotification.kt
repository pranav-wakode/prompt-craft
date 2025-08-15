package com.pranav.promptcraft.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class InAppNotification(
    val id: Int,
    val title: String,
    val message: String,
    val url: String,
    val timestamp: String
)
