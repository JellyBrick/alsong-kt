package be.zvz.alsong

import be.zvz.alsong.dto.LyricUpload
import be.zvz.alsong.dto.LyricUploadResult
import be.zvz.alsong.serializer.LyricSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.test.Test
import kotlin.test.assertTrue

class AlsongTest {
    private fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
        val naiveTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }

        val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
            val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

        sslSocketFactory(insecureSocketFactory, naiveTrustManager)
        hostnameVerifier { _, _ -> true }
        return this
    }

    @Test fun testParseSOAP() {
        val testString = """<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <soap:Body>
        <UploadLyricResponse xmlns="ALSongWebServer">
            <UploadLyricResult>ModifySuccessed</UploadLyricResult>
        </UploadLyricResponse>
    </soap:Body>
</soap:Envelope>"""
        val xml = XML {
            xmlDeclMode = XmlDeclMode.Charset
            autoPolymorphic = true
        }
        val serializer = serializer<LyricUploadResult>()
        val deserialized: LyricUploadResult = xml.decodeFromString(serializer, testString)
        println(xml.encodeToString(deserialized))
    }

    @Test
    fun testLyricSerializer() {
        val map = mapOf(
            0L to listOf(
                "そして君が知らずに幸せな灰になった後で",
                "소시테 키미가 시라즈니 시아와세나 하이니 낫타 아토데",
                "그리고 네가 모르는 행복한 재가 되어버린 후에",
            ),
            2006L to listOf(
                "僕は今更君が好きだって",
                "보쿠와 이마사라 키미가 스키닷테",
                "나는 이제서야 네가 좋다고",
            ),
            2853L to listOf(
                "大人になりたくないよなんて大人ぶってさ",
                "오토나니 나리타쿠나이요 난떼 오토나붓테사",
                "'어른이 되고 싶지 않아'라며 어른인 척 하면서",
            ),
        )

        val xml = XML {
            xmlDeclMode = XmlDeclMode.Charset
            autoPolymorphic = true
        }

        @Serializable
        data class TestClass(
            @Serializable(with = LyricSerializer::class)
            @XmlElement(true)
            val lyric: Map<Long, List<String>>,
        )

        println(xml.encodeToString(TestClass(map)))
    }

    @Test
    fun testLyricDeserializer() {
        val testString = """<?xml version="1.1" encoding="UTF-8"?>
<SOAP-ENV:Envelope
    xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope">
    <SOAP-ENV:Body>
        <ns1:UploadLyric
            xmlns:ns1="ALSongWebServer">
            <ns1:stQuery>
                <ns1:nUploadLyricType>2</ns1:nUploadLyricType>
                <ns1:strMD5>MD5</ns1:strMD5>
                <ns1:strRegisterFirstName>FirstName</ns1:strRegisterFirstName>
                <ns1:strRegisterFirstEMail></ns1:strRegisterFirstEMail>
                <ns1:strRegisterFirstURL></ns1:strRegisterFirstURL>
                <ns1:strRegisterFirstPhone></ns1:strRegisterFirstPhone>
                <ns1:strRegisterFirstComment></ns1:strRegisterFirstComment>
                <ns1:strRegisterName>Name</ns1:strRegisterName>
                <ns1:strRegisterEMail></ns1:strRegisterEMail>
                <ns1:strRegisterURL></ns1:strRegisterURL>
                <ns1:strRegisterPhone></ns1:strRegisterPhone>
                <ns1:strRegisterComment></ns1:strRegisterComment>
                <ns1:strFileName>FileName</ns1:strFileName>
                <ns1:strTitle>Title</ns1:strTitle>
                <ns1:strArtist>Artist</ns1:strArtist>
                <ns1:strAlbum>Album</ns1:strAlbum>
                <ns1:nInfoID>-1</ns1:nInfoID>
                <ns1:strLyric>[00:00.36]流れ?く空と日?の?間に&lt;br&gt;[00:00.36]나가레츠즈쿠 소라토 히비노 하자마니&lt;br&gt;[00:00.36]계속 이어지는 하늘과 나날들의 사이에&lt;br&gt;[00:03.90]形のない今日をそれでも進む&lt;br&gt;[00:03.90]카타치노나이 쿄-오 소레데모 스스무&lt;br&gt;[00:03.90]형태없는 오늘을 그래도 나아가&lt;br&gt;</ns1:strLyric>
                <ns1:nPlayTime>100</ns1:nPlayTime>
                <ns1:strVersion>40040401</ns1:strVersion>
                <ns1:strMACAddress>Google_armeabi-v7a</ns1:strMACAddress>
                <ns1:strIPAddress>unknown</ns1:strIPAddress>
            </ns1:stQuery>
        </ns1:UploadLyric>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>"""
        val xml = XML {
            xmlDeclMode = XmlDeclMode.Charset
            autoPolymorphic = true
        }

        println(
            xml.decodeFromString<LyricUpload>(testString),
        )
    }

    @Test fun testGetResembleLyricList() {
        val classUnderTest = Alsong(
            okHttpClient = OkHttpClient.Builder().ignoreAllSSLErrors().build(),
        )
        val lyricList = classUnderTest.getResembleLyricList(
            artist = "IU",
            title = "이런 엔딩",
        )
        assertTrue(lyricList.isNotEmpty(), "lyricList is empty")
        println(
            lyricList,
        )
    }

    @Test fun testGetLyricById() {
        val classUnderTest = Alsong(
            okHttpClient = OkHttpClient.Builder().ignoreAllSSLErrors().build(),
        )
        val lyricList = classUnderTest.getResembleLyricList(
            artist = "IU",
            title = "Love Poem",
        )
        assertTrue(lyricList.isNotEmpty(), "lyricList is empty")
        println(
            classUnderTest.getLyricById(lyricList.first().lyricId),
        )
    }

    @Test fun testGetLyricByHash() {
        val classUnderTest = Alsong(
            okHttpClient = OkHttpClient.Builder().ignoreAllSSLErrors().build(),
        )
        println(
            classUnderTest.getLyricByHash("6ab8bfe86f2755774dc8986e8bdff2f0"),
        )
    }

    @Test fun testGetMultiLineLyricByHash() {
        val classUnderTest = Alsong(
            okHttpClient = OkHttpClient.Builder().ignoreAllSSLErrors().build(),
        )
        println(
            classUnderTest.getLyricByHash("9059c9f520838290f091eb528ac04855"),
        )
    }

    @Test fun testGetLyricByMurekaId() {
        val classUnderTest = Alsong(
            okHttpClient = OkHttpClient.Builder().ignoreAllSSLErrors().build(),
        )
        println(
            classUnderTest.getLyricByMurekaId(101547527),
        )
    }
}
