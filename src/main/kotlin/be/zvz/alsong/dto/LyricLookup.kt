package be.zvz.alsong.dto

import be.zvz.alsong.serializer.LyricSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LyricLookup(
    val registerUrl: String?,
    @SerialName("lyric")
    @Serializable(with = LyricSerializer::class)
    val lyrics: Map<Long, List<String>>,
    val albumName: String,
    val infoId: Long,
    val countGood: Long,
    val countBad: Long,
    val statusId: Long,
    val registerFirstEmail: String?,
    val registerComment: String?,
    val registerName: String?,
    val registerFirstName: String?,
    val registerFirstUrl: String?,
    val registDate: String,
    val registerPhone: String?,
    val registerFirstPhone: String?,
    val artist: String,
    val title: String,
    val registerFirstComment: String?,
    val registerEmail: String?,
)
