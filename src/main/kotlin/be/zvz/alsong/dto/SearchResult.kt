package be.zvz.alsong.dto

import be.zvz.alsong.serializer.DateSerializer
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class SearchResult(
    val lyricId: Long,
    val playtime: Long,
    val artist: String,
    val album: String,
    val title: String,
    @Serializable(with = DateSerializer::class)
    val registerDate: Date? = null,
)
