package be.zvz.alsong.dto

import be.zvz.alsong.serializer.LyricSerializer
import kotlinx.serialization.Serializable

@Serializable
data class LyricMurekaId(
    @Serializable(with = LyricSerializer::class)
    val lyric: Map<Long, List<String>>,
)
