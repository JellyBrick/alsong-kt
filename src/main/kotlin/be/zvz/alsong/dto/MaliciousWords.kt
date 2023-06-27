package be.zvz.alsong.dto

import kotlinx.serialization.Serializable

@Serializable
data class MaliciousWords(
    val asteriskCount: Long,
    val maliciousWords: List<String>,
)
