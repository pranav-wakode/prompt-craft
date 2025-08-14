package com.pranav.promptcraft.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Domain model representing a prompt with its original and enhanced versions.
 */
@Entity(tableName = "prompts")
data class Prompt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalPrompt: String,
    val enhancedPrompt: String,
    val promptTypes: List<String>,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
