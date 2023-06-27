package be.zvz.alsong.dto

import be.zvz.alsong.serializer.LyricSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "Envelope", namespace = "http://www.w3.org/2003/05/soap-envelope", prefix = "SOAP-ENV")
data class LyricUpload(
    val body: Body,
) {
    @Serializable
    data class Body(
        @XmlSerialName(value = "UploadLyric", namespace = "ALSongWebServer", prefix = "ns1")
        val uploadLyric: UploadLyric,
    ) {
        @Serializable
        data class UploadLyric(
            @XmlSerialName(value = "stQuery")
            val stQuery: StQuery,
        ) {
            @Serializable
            data class StQuery(
                @XmlSerialName(value = "nUploadLyricType")
                @XmlElement(true)
                val uploadLyricType: Long,
                @XmlSerialName(value = "strMD5")
                @XmlElement(true)
                val md5: String,
                @XmlSerialName(value = "strRegisterFirstName")
                @XmlElement(true)
                val registerFirstName: String,
                @XmlSerialName(value = "strRegisterFirstEMail")
                @XmlElement(true)
                val registerFirstEmail: String = "",
                @XmlSerialName(value = "strRegisterFirstURL")
                @XmlElement(true)
                val registerFirstUrl: String = "",
                @XmlSerialName(value = "strRegisterFirstPhone")
                @XmlElement(true)
                val registerFirstPhone: String = "",
                @XmlSerialName(value = "strRegisterFirstComment")
                @XmlElement(true)
                val registerFirstComment: String = "",
                @XmlSerialName(value = "strRegisterName")
                @XmlElement(true)
                val registerName: String,
                @XmlSerialName(value = "strRegisterEMail")
                @XmlElement(true)
                val registerEmail: String,
                @XmlSerialName(value = "strRegisterURL")
                @XmlElement(true)
                val registerUrl: String = "",
                @XmlSerialName(value = "strRegisterPhone")
                @XmlElement(true)
                val registerPhone: String = "",
                @XmlSerialName(value = "strRegisterComment")
                @XmlElement(true)
                val registerComment: String = "",
                @XmlSerialName(value = "strFileName")
                @XmlElement(true)
                val fileName: String,
                @XmlSerialName(value = "strTitle")
                @XmlElement(true)
                val title: String,
                @XmlSerialName(value = "strArtist")
                @XmlElement(true)
                val artist: String,
                @XmlSerialName(value = "strAlbum")
                @XmlElement(true)
                val album: String = "",
                @XmlSerialName(value = "nInfoID")
                @XmlElement(true)
                val infoId: Long,
                @Serializable(with = LyricSerializer::class)
                @XmlSerialName(value = "strLyric")
                @XmlElement(true)
                val lyrics: Map<Long, List<String>>,
                @XmlSerialName(value = "nPlayTime")
                @XmlElement(true)
                val playtime: Long,
                @XmlSerialName(value = "strVersion")
                @XmlElement(true)
                val version: String,
                @XmlSerialName(value = "strMACAddress")
                @XmlElement(true)
                val macAddress: String,
                @XmlSerialName(value = "strIPAddress")
                @XmlElement(true)
                val ipAddress: String,
            )
        }
    }
}
