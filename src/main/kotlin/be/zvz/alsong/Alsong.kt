package be.zvz.alsong

import be.zvz.alsong.dto.LyricCount
import be.zvz.alsong.dto.LyricInfo
import be.zvz.alsong.dto.LyricLookup
import be.zvz.alsong.dto.LyricMurekaId
import be.zvz.alsong.dto.LyricUpload
import be.zvz.alsong.dto.LyricUploadResult
import be.zvz.alsong.dto.MaliciousWords
import be.zvz.alsong.dto.SearchResult
import be.zvz.alsong.dto.UserData
import be.zvz.alsong.exception.InvalidDataReceivedException
import be.zvz.alsong.exception.NoDataReceivedException
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.serialization.responseObject
import com.github.kittinunf.result.getOrElse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import com.github.kittinunf.result.Result as FuelResult

class Alsong
@OptIn(ExperimentalSerializationApi::class)
@JvmOverloads
constructor(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        namingStrategy = JsonNamingStrategy.SnakeCase
        allowStructuredMapKeys = true
    },
    private val xml: XML = XML {
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
    private val fuelManager: FuelManager = FuelManager(),
) {
    init {
        fuelManager.basePath = Utils.BASE_URL
        fuelManager.baseHeaders = mapOf(
            "User-Agent" to "android",
        )
    }

    @JvmOverloads
    fun getResembleLyricList(
        artist: String,
        title: String,
        playtime: Int = 0,
        page: Int = 1,
    ): List<SearchResult> = handleResponseOrThrow(
        fuelManager.post(
            "/v1/search",
            mutableListOf(
                "title" to title,
                "artist" to artist,
                "page" to page,
                "encData" to Utils.encKey,
            ).apply {
                if (playtime != 0) {
                    add("playtime" to playtime)
                }
            },
        )
            .responseObject<List<SearchResult>>(ListSerializer(SearchResult.serializer()), json)
            .third,
    )

    @JvmOverloads
    fun getResembleLyricList(
        artist: String,
        title: String,
        playtime: Int = 0,
        page: Int = 1,
        onSuccess: (List<SearchResult>) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = fuelManager.post(
        "/v1/search",
        mutableListOf(
            "title" to title,
            "artist" to artist,
            "page" to page,
            "encData" to Utils.encKey,
        ).apply {
            if (playtime != 0) {
                add("playtime" to playtime)
            }
        },
    )
        .responseObject<List<SearchResult>>(ListSerializer(SearchResult.serializer()), json) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    @JvmOverloads
    fun getResembleLyricListCount(
        artist: String,
        title: String,
        playtime: Int = 0,
    ): LyricCount = handleResponseOrThrow(
        fuelManager.post(
            "/v1/search/count",
            mutableListOf(
                "title" to title,
                "artist" to artist,
                "encData" to Utils.encKey,
            ).apply {
                if (playtime != 0) {
                    add("playtime" to playtime.toString())
                }
            },
        )
            .responseObject<LyricCount>(json)
            .third,
    )

    @JvmOverloads
    fun getResembleLyricListCount(
        artist: String,
        title: String,
        playtime: Int = 0,
        onSuccess: (LyricCount) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = fuelManager.post(
        "/v1/search/count",
        mutableListOf(
            "title" to title,
            "artist" to artist,
            "encData" to Utils.encKey,
        ).apply {
            if (playtime != 0) {
                add("playtime" to playtime.toString())
            }
        },
    )
        .responseObject<LyricCount>(json) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    fun getLyricById(
        lyricId: Long,
    ): LyricInfo = handleResponseOrThrow(
        fuelManager.post(
            "/v1/info",
            listOf(
                "info_id" to lyricId,
                "encData" to Utils.encKey,
            ),
        )
            .responseObject<LyricInfo>(json)
            .third,
    )

    @JvmOverloads
    fun getLyricById(
        lyricId: Long,
        onSuccess: (LyricInfo) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = fuelManager.post(
        "/v1/info",
        listOf(
            "info_id" to lyricId,
            "encData" to Utils.encKey,
        ),
    )
        .responseObject<LyricInfo>(json) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    fun getLyricByHash(
        md5: String,
    ): LyricLookup = handleResponseOrThrow(
        fuelManager.post(
            "/v1/lookup",
            listOf(
                "md5" to md5,
                "encData" to Utils.encKey,
            ),
        )
            .responseObject<LyricLookup>(json)
            .third,
    )

    @JvmOverloads
    fun getLyricByHash(
        md5: String,
        onSuccess: (LyricLookup) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = fuelManager.post(
        "/v1/lookup",
        listOf(
            "md5" to md5,
            "encData" to Utils.encKey,
        ),
    )
        .responseObject<LyricLookup>(json) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    fun getLyricByMurekaId(
        murekaId: Long,
    ): List<LyricMurekaId> = handleResponseOrThrow(
        fuelManager.post(
            "/v1/lookupListByMurekaId",
            listOf(
                "murekaid" to murekaId,
                "encData" to Utils.encKey,
            ),
        )
            .responseObject<List<LyricMurekaId>>(ListSerializer(LyricMurekaId.serializer()), json)
            .third,
    )

    @JvmOverloads
    fun getLyricByMurekaId(
        murekaId: Long,
        onSuccess: (List<LyricMurekaId>) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = fuelManager.post(
        "/v1/lookupListByMurekaId",
        listOf(
            "murekaid" to murekaId,
            "encData" to Utils.encKey,
        ),
    )
        .responseObject<List<LyricMurekaId>>(ListSerializer(LyricMurekaId.serializer()), json) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    private fun generateUploadLyricPostRequest(
        isModifying: Boolean,
        lyric: Map<Long, List<String>>,
        md5: String,
        registerData: UserData,
        fileName: String,
        title: String,
        artist: String,
        album: String = "",
        playtime: Long = -1,
        originalLyricId: Long = -1,
    ) = fuelManager.post("https://lyrics.alsong.co.kr/alsongwebservice/service1.asmx")
        .header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 10; Pixel 3a Build/QQ3A.200805.001)")
        .body(
            xml.encodeToString(
                LyricUpload(
                    LyricUpload.Body(
                        LyricUpload.Body.UploadLyric(
                            LyricUpload.Body.UploadLyric.StQuery(
                                uploadLyricType = if (isModifying) 2 else 0,
                                md5 = md5,
                                registerFirstName = registerData.firstName,
                                registerFirstEmail = registerData.firstEmail,
                                registerFirstUrl = registerData.firstUrl,
                                registerFirstPhone = registerData.firstPhone,
                                registerFirstComment = registerData.firstComment,
                                registerName = registerData.name,
                                registerEmail = registerData.email,
                                registerUrl = registerData.url,
                                registerPhone = registerData.phone,
                                registerComment = registerData.comment,
                                fileName = fileName,
                                title = title,
                                artist = artist,
                                album = album,
                                infoId = if (isModifying) originalLyricId else -1,
                                lyrics = lyric,
                                playtime = playtime,
                                version = "40040401",
                                macAddress = "Google_armeabi-v7a",
                                ipAddress = "unknown",
                            ),
                        ),
                    ),
                ),
            ),
        )

    @JvmOverloads
    fun uploadLyric(
        isModifying: Boolean,
        lyric: Map<Long, List<String>>,
        md5: String,
        registerData: UserData,
        fileName: String,
        title: String,
        artist: String,
        album: String = "",
        playtime: Long = -1,
        originalLyricId: Long = -1,
    ): LyricUploadResult {
        val result = handleResponseOrThrow(
            generateUploadLyricPostRequest(
                isModifying,
                lyric,
                md5,
                registerData,
                fileName,
                title,
                artist,
                album,
                playtime,
                originalLyricId,
            )
                .responseObject<LyricUploadResult>(json)
                .third,
        )
        fuelManager.get(
            "https://alsong-stats.altools.com/v1/${
                if (isModifying) "request-modifing-lyric-log" else "new-lyric-log"
            }",
        ).response { _, _, _ -> }
        return result
    }

    @JvmOverloads
    fun uploadLyric(
        isModifying: Boolean,
        lyric: Map<Long, List<String>>,
        md5: String,
        registerData: UserData,
        fileName: String,
        title: String,
        artist: String,
        album: String = "",
        playtime: Long = -1,
        originalLyricId: Long = -1,
        onSuccess: (LyricUploadResult) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = generateUploadLyricPostRequest(
        isModifying,
        lyric,
        md5,
        registerData,
        fileName,
        title,
        artist,
        album,
        playtime,
        originalLyricId,
    )
        .responseObject<LyricUploadResult>(json) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching {
                    fuelManager.get(
                        "https://alsong-stats.altools.com/v1/${
                            if (isModifying) "request-modifing-lyric-log" else "new-lyric-log"
                        }",
                    ).response { _, _, _ -> }
                    onSuccess(it)
                }
                .getOrElse { onFailure?.invoke(it) }
        }

    fun getMaliciousWords(): MaliciousWords = handleResponseOrThrow(
        fuelManager.get("https://aldn.altools.co.kr/alsong/maliciouswords.json")
            .responseObject<MaliciousWords>(json)
            .third,
    )

    @JvmOverloads
    fun getMaliciousWords(
        onSuccess: (MaliciousWords) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = fuelManager.get("https://aldn.altools.co.kr/alsong/maliciouswords.json")
        .responseObject<MaliciousWords>(json) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    fun matchLyric(
        md5: String,
        lyricId: Long,
    ) {
        fuelManager.post(
            "/v1/matchLyric",
            listOf(
                "encData" to Utils.encKey,
                "md5" to md5,
                "info_id" to lyricId,
            ),
        )
            .responseString()
    }

    @JvmOverloads
    fun matchLyric(
        md5: String,
        lyricId: Long,
        onSuccess: (String) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = fuelManager.post(
        "/v1/matchLyric",
        listOf(
            "encData" to Utils.encKey,
            "md5" to md5,
            "info_id" to lyricId,
        ),
    )
        .responseString { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    private fun <T> handleResponse(result: FuelResult<T, FuelError>): T =
        result.getOrElse {
            throw InvalidDataReceivedException(it.exception)
        }

    private fun <T> handleResponseOrThrow(result: FuelResult<T, FuelError>): T =
        handleResponse(result) ?: throw NoDataReceivedException()
}
