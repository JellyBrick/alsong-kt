package be.zvz.alsong

import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import javax.crypto.Cipher

object Utils {
    private const val KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfvB8/TBDhfgES1y54kW2lBu3VfaBurGrk8A3TAQZxeAV7qpupTvbmZb+ynO5WfeQIEknAvjdvmBE4PObRK610Si8S/BYYnD1uwEEiK0WVQYQWXzfZjRiO1a0Vj/i1AE6OcX9xT8liq36wLVhIGWDU1i8JwLZC5JbscD7KHGU3SwIDAQAB"
    const val BASE_URL = "https://lyric.altools.com"

    private val PUBLIC_KEY = KeyFactory
        .getInstance("RSA")
        .generatePublic(
            X509EncodedKeySpec(
                Base64.getDecoder().decode(KEY),
            ),
        )
    private val DATE_FORMAT = SimpleDateFormat("_yyyyMMdd_HHmmss")
    private val CIPHER = Cipher.getInstance("RSA/ECB/PKCS1Padding").apply {
        init(Cipher.ENCRYPT_MODE, PUBLIC_KEY)
    }

    val encKey: String
        get() = buildString {
            CIPHER.doFinal(
                "ALSONG_ANDROID${DATE_FORMAT.format(Date())}".encodeToByteArray(),
            ).forEach { append("%02X".format(it)) }
        }
}
