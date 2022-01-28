package be.zvz.alsong

import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.TreeMap
import javax.crypto.Cipher

object Utils {
    private const val KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfvB8/TBDhfgES1y54kW2lBu3VfaBurGrk8A3TAQZxeAV7qpupTvbmZb+ynO5WfeQIEknAvjdvmBE4PObRK610Si8S/BYYnD1uwEEiK0WVQYQWXzfZjRiO1a0Vj/i1AE6OcX9xT8liq36wLVhIGWDU1i8JwLZC5JbscD7KHGU3SwIDAQAB"
    const val BASE_URL = "https://lyric.altools.com"

    private val BYTE_KEY = Base64.getDecoder().decode(KEY)
    private val KEY_SPEC = X509EncodedKeySpec(BYTE_KEY)
    private val KEY_FACTORY = KeyFactory.getInstance("RSA")
    private val PUBLIC_KEY = KEY_FACTORY.generatePublic(KEY_SPEC)
    private val LYRIC_REGEX = Regex("^(.)?\\[(\\d+):(\\d\\d).(\\d\\d)](.*)\$")

    fun parseLyric(lyrics: String): Map<Long, List<String>> = TreeMap<Long, MutableList<String>>().apply {
        lyrics.split("<br>").forEach {
            LYRIC_REGEX.matchEntire(it)?.let { match ->
                val groupValues = match.groupValues
                val timestamp = 10 * groupValues[2].toLong() * 60 * 100 + groupValues[3].toLong() * 100 + groupValues[4].toLong()
                if (this.containsKey(timestamp)) {
                    this.getValue(timestamp).add(groupValues[5])
                } else {
                    this[timestamp] = mutableListOf<String>().apply {
                        add(groupValues[5])
                    }
                }
            }
        }
    }

    fun getEncKey(): String {
        val dateFormat = SimpleDateFormat("_yyyyMMdd_HHmmss")
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, PUBLIC_KEY)
        val data = "ALSONG_ANDROID${dateFormat.format(Date())}".encodeToByteArray()
        return cipher.doFinal(data).joinToString("") { "%02x".format(it) }.uppercase()
    }
}
