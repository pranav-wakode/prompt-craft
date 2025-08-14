package com.pranav.promptcraft.domain.model

/**
 * Enum representing different prompt engineering techniques
 */
enum class PromptType(val displayName: String) {
    AUTO("Auto"),
    ZERO_SHOT("Zero-Shot"),
    FEW_SHOT("Few-Shot"),
    CHAIN_OF_THOUGHT("Chain-of-Thought"),
    ROLE_PLAYING("Role-Playing"),
    INSTRUCTIONAL("Instructional"),
    CREATIVE("Creative"),
    ANALYTICAL("Analytical")
}
