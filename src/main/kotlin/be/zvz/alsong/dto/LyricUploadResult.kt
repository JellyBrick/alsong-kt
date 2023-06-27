package be.zvz.alsong.dto

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "Envelope", namespace = "http://www.w3.org/2003/05/soap-envelope", prefix = "soap")
data class LyricUploadResult(
    val body: Body,
) {
    @Serializable
    data class Body(
        @XmlSerialName(value = "UploadLyricResponse", namespace = "ALSongWebServer", prefix = "")
        val uploadLyricResponse: UploadLyricResponse,
    ) {
        @Serializable
        data class UploadLyricResponse(
            @XmlSerialName(value = "UploadLyricResult")
            @XmlElement(true)
            val uploadLyricResult: String,
        )
    }
}
