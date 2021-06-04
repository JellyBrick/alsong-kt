package be.zvz.alsong.dto

import be.zvz.alsong.deserializer.LyricDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class LyricMurekaId(
    @JsonDeserialize(contentUsing = LyricDeserializer::class)
    val lyric: List<Map<Long, List<String>>>
)
