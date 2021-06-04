package be.zvz.alsong.dto

import be.zvz.alsong.deserializer.LyricDeserializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class LyricLookup(
    val registerUrl: String,
    @JsonProperty("lyric")
    @JsonDeserialize(using = LyricDeserializer::class)
    val lyrics: Map<Long, List<String>>,
    val albumName: String,
    val infoId: Long,
    val countGood: Long,
    val countBad: Long,
    val statusId: Long,
    val registerFirstEmail: String,
    val registerComment: String,
    val registerName: String,
    val registerFirstName: String,
    val registerFirstUrl: String,
    val registDate: String,
    val registerPhone: String,
    val registerFirstPhone: String,
    val artist: String,
    val title: String,
    val registerFirstComment: String,
    val registerEmail: String
)
