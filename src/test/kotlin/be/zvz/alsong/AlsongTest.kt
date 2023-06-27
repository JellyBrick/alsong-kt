package be.zvz.alsong

import be.zvz.alsong.dto.LyricUploadResult
import be.zvz.alsong.serializer.LyricSerializer
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.test.Test
import kotlin.test.assertTrue

class AlsongTest {
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

    @Test fun testGetResembleLyricList() {
        val classUnderTest = Alsong(
            fuelManager = FuelManager().apply {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                })

                socketFactory = SSLContext.getInstance("SSL").apply {
                    init(null, trustAllCerts, java.security.SecureRandom())
                }.socketFactory

                hostnameVerifier = HostnameVerifier { _, _ -> true }
            },
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
            fuelManager = FuelManager().apply {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                })

                socketFactory = SSLContext.getInstance("SSL").apply {
                    init(null, trustAllCerts, java.security.SecureRandom())
                }.socketFactory

                hostnameVerifier = HostnameVerifier { _, _ -> true }
            },
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
            fuelManager = FuelManager().apply {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                })

                socketFactory = SSLContext.getInstance("SSL").apply {
                    init(null, trustAllCerts, java.security.SecureRandom())
                }.socketFactory

                hostnameVerifier = HostnameVerifier { _, _ -> true }
            },
        )
        println(
            classUnderTest.getLyricByHash("6ab8bfe86f2755774dc8986e8bdff2f0"),
        )
    }

    @Test fun testGetLyricByMurekaId() {
        val classUnderTest = Alsong(
            fuelManager = FuelManager().apply {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                })

                socketFactory = SSLContext.getInstance("SSL").apply {
                    init(null, trustAllCerts, java.security.SecureRandom())
                }.socketFactory

                hostnameVerifier = HostnameVerifier { _, _ -> true }
            },
        )
        println(
            classUnderTest.getLyricByMurekaId(101547527),
        )
    }
}
