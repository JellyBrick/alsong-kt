package be.zvz.alsong.dto

import be.zvz.alsong.serializer.LyricSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LyricInfo(
    val registerUrl: String,
    @SerialName("lyric")
    @Serializable(with = LyricSerializer::class)
    val lyrics: Map<Long, List<String>>,
    val albumName: String,
    val infoId: Long,
    val registerFirstEmail: String?,
    val registerComment: String?,
    val registerName: String?,
    val registerFirstName: String?,
    val registerFirstUrl: String?,
    val registerPhone: String?,
    val artistName: String,
    val registerFirstPhone: String?,
    val titleName: String,
    val registerFirstComment: String?,
    val registerEmail: String?,
)
