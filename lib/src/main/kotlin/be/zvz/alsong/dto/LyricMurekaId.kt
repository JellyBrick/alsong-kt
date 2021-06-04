package be.zvz.alsong.dto

import be.zvz.alsong.deserializer.LyricDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class LyricMurekaId(
    @JsonDeserialize(using = LyricDeserializer::class)
    val lyric: Map<Long, List<String>>
)
