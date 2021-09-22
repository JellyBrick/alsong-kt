package be.zvz.alsong.dto

import be.zvz.alsong.deserializer.LyricDeserializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class LyricInfo(
    val registerUrl: String,
    @JsonProperty("lyric")
    @JsonDeserialize(using = LyricDeserializer::class)
    val lyrics: Map<Long, List<String>>,
    val albumName: String,
    val infoId: Long,
    val registerFirstEmail: String?,
    val registerComment: String,
    val registerName: String,
    val registerFirstName: String,
    val registerFirstUrl: String,
    val registerPhone: String,
    val artistName: String,
    val registerFirstPhone: String?,
    val titleName: String,
    val registerFirstComment: String,
    val registerEmail: String
)
