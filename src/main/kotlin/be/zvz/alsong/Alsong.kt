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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.decodeFromStream
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.KtXmlReader
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

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
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
    private val okHttpClient: OkHttpClient = OkHttpClient(),
) {
    private val defaultHttpUrl = Utils.BASE_URL.toHttpUrl()

    private fun createResembleLyricListRequest(
        artist: String,
        title: String,
        playtime: Int = 0,
        page: Int = 1,
    ) = okHttpClient.newCall(
        Request.Builder()
            .header("User-Agent", "android")
            .url(defaultHttpUrl.resolve("/v1/search")!!)
            .post(
                FormBody.Builder()
                    .add("title", title)
                    .add("artist", artist)
                    .add("page", page.toString())
                    .add("encData", Utils.encKey)
                    .apply {
                        if (playtime != 0) {
                            add("playtime", playtime.toString())
                        }
                    }
                    .build(),
            )
            .build(),
    )

    @OptIn(ExperimentalSerializationApi::class)
    @JvmOverloads
    fun getResembleLyricList(
        artist: String,
        title: String,
        playtime: Int = 0,
        page: Int = 1,
    ): List<SearchResult> = handleResponseOrThrow(
        createResembleLyricListRequest(
            artist,
            title,
            playtime,
            page,
        ).execute(),
        json,
    )

    @JvmOverloads
    fun getResembleLyricList(
        artist: String,
        title: String,
        playtime: Int = 0,
        page: Int = 1,
        onSuccess: (List<SearchResult>) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = createResembleLyricListRequest(
        artist,
        title,
        playtime,
        page,
    ).enqueue(AsyncJsonCallback(onSuccess, onFailure, json))

    fun createResembleLyricListCountRequest(
        artist: String,
        title: String,
        playtime: Int = 0,
    ) = okHttpClient.newCall(
        Request.Builder()
            .header("User-Agent", "android")
            .url(defaultHttpUrl.resolve("/v1/search/count")!!)
            .post(
                FormBody.Builder()
                    .add("title", title)
                    .add("artist", artist)
                    .add("encData", Utils.encKey)
                    .apply {
                        if (playtime != 0) {
                            add("playtime", playtime.toString())
                        }
                    }
                    .build(),
            )
            .build(),
    )

    @JvmOverloads
    fun getResembleLyricListCount(
        artist: String,
        title: String,
        playtime: Int = 0,
    ): LyricCount = handleResponseOrThrow(
        createResembleLyricListCountRequest(
            artist,
            title,
            playtime,
        ).execute(),
        json,
    )

    @JvmOverloads
    fun getResembleLyricListCount(
        artist: String,
        title: String,
        playtime: Int = 0,
        onSuccess: (LyricCount) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = createResembleLyricListCountRequest(
        artist,
        title,
        playtime,
    ).enqueue(AsyncJsonCallback(onSuccess, onFailure, json))

    private fun createLyricByIdRequest(
        lyricId: Long,
    ) = okHttpClient.newCall(
        Request.Builder()
            .header("User-Agent", "android")
            .url(defaultHttpUrl.resolve("/v1/info")!!)
            .post(
                FormBody.Builder()
                    .add("info_id", lyricId.toString())
                    .add("encData", Utils.encKey)
                    .build(),
            )
            .build(),
    )

    fun getLyricById(
        lyricId: Long,
    ): LyricInfo = handleResponseOrThrow(
        createLyricByIdRequest(
            lyricId,
        ).execute(),
        json,
    )

    @JvmOverloads
    fun getLyricById(
        lyricId: Long,
        onSuccess: (LyricInfo) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = createLyricByIdRequest(
        lyricId,
    ).enqueue(AsyncJsonCallback(onSuccess, onFailure, json))

    private fun createLyricByHash(
        md5: String,
    ) = okHttpClient.newCall(
        Request.Builder()
            .header("User-Agent", "android")
            .url(defaultHttpUrl.resolve("/v1/lookup")!!)
            .post(
                FormBody.Builder()
                    .add("md5", md5)
                    .add("encData", Utils.encKey)
                    .build(),
            )
            .build(),
    )

    fun getLyricByHash(
        md5: String,
    ): LyricLookup = handleResponseOrThrow(
        createLyricByHash(
            md5,
        ).execute(),
        json,
    )

    @JvmOverloads
    fun getLyricByHash(
        md5: String,
        onSuccess: (LyricLookup) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = createLyricByHash(
        md5,
    ).enqueue(AsyncJsonCallback(onSuccess, onFailure, json))

    private fun createLyricByMurekaIdRequest(
        murekaId: Long,
    ) = okHttpClient.newCall(
        Request.Builder()
            .header("User-Agent", "android")
            .url(defaultHttpUrl.resolve("/v1/lookupListByMurekaId")!!)
            .post(
                FormBody.Builder()
                    .add("murekaid", murekaId.toString())
                    .add("encData", Utils.encKey)
                    .build(),
            )
            .build(),
    )

    fun getLyricByMurekaId(
        murekaId: Long,
    ): List<LyricMurekaId> = handleResponseOrThrow(
        createLyricByMurekaIdRequest(
            murekaId,
        ).execute(),
        json,
    )

    @JvmOverloads
    fun getLyricByMurekaId(
        murekaId: Long,
        onSuccess: (List<LyricMurekaId>) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = createLyricByMurekaIdRequest(
        murekaId,
    ).enqueue(AsyncJsonCallback(onSuccess, onFailure, json))

    private fun createUploadLyricPostRequest(
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
    ) = okHttpClient.newCall(
        Request.Builder()
            .url("http://lyrics.alsong.co.kr/alsongwebservice/service1.asmx")
            .header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 10; Pixel 3a Build/QQ3A.200805.001)")
            .post(
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
                ).toRequestBody("application/soap+xml".toMediaType()),
            ).build(),
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
        val result: LyricUploadResult = handleResponseOrThrow(
            createUploadLyricPostRequest(
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
            ).execute(),
            xml,
        )
        okHttpClient.newCall(
            Request.Builder()
                .url(
                    "https://alsong-stats.altools.com/v1/${
                        if (isModifying) "request-modifing-lyric-log" else "new-lyric-log"
                    }",
                ).get()
                .build(),
        ).execute()
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
    ) = createUploadLyricPostRequest(
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
        .enqueue(
            AsyncXmlCallback<LyricUploadResult>({
                okHttpClient.newCall(
                    Request.Builder()
                        .url(
                            "https://alsong-stats.altools.com/v1/${
                                if (isModifying) "request-modifing-lyric-log" else "new-lyric-log"
                            }",
                        ).get()
                        .build(),
                ).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onFailure?.invoke(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        onSuccess(it)
                    }
                })
            }, onFailure, xml),
        )

    private fun createMaliciousWordsRequest() = okHttpClient.newCall(
        Request.Builder()
            .header("User-Agent", "android")
            .url("https://aldn.altools.co.kr/alsong/maliciouswords.json")
            .build(),
    )

    fun getMaliciousWords(): MaliciousWords = handleResponseOrThrow(
        createMaliciousWordsRequest().execute(),
        json,
    )

    @JvmOverloads
    fun getMaliciousWords(
        onSuccess: (MaliciousWords) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = createMaliciousWordsRequest().enqueue(AsyncJsonCallback(onSuccess, onFailure, json))

    private fun createMatchLyricRequest(
        md5: String,
        lyricId: Long,
    ) = okHttpClient.newCall(
        Request.Builder()
            .header("User-Agent", "android")
            .url("https://aldn.altools.co.kr/alsong/matchlyric.json")
            .post(
                FormBody.Builder()
                    .add("encData", Utils.encKey)
                    .add("md5", md5)
                    .add("info_id", lyricId.toString())
                    .build(),
            )
            .build(),
    )

    fun matchLyric(
        md5: String,
        lyricId: Long,
    ): String = createMatchLyricRequest(md5, lyricId).execute().body.string()

    @JvmOverloads
    fun matchLyric(
        md5: String,
        lyricId: Long,
        onSuccess: (String) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null,
    ) = createMatchLyricRequest(md5, lyricId).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onFailure?.invoke(e)
        }

        override fun onResponse(call: Call, response: Response) {
            onSuccess(response.body.string())
        }
    })

    @OptIn(ExperimentalXmlUtilApi::class)
    private inline fun <reified T> handleResponse(result: Response, serializer: XML): T = when {
        !result.isSuccessful -> throw InvalidDataReceivedException(result)
        else -> serializer.decodeFromReader(KtXmlReader(result.body.charStream()))
    }

    private inline fun <reified T> handleResponseOrThrow(result: Response, serializer: XML): T =
        handleResponse(result, serializer) ?: throw NoDataReceivedException()

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T> handleResponse(result: Response, serializer: Json): T = when {
        !result.isSuccessful -> throw InvalidDataReceivedException(result)
        else -> serializer.decodeFromStream(result.body.byteStream())
    }

    private inline fun <reified T> handleResponseOrThrow(result: Response, serializer: Json): T =
        handleResponse(result, serializer) ?: throw NoDataReceivedException()

    private inner class AsyncJsonCallback<T>(
        private val onSuccess: ((T) -> Unit),
        private val onFailure: ((Throwable) -> Unit)?,
        private val json: Json,
    ) : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onFailure?.invoke(e)
        }

        override fun onResponse(call: Call, response: Response) {
            runCatching { return@onResponse handleResponseOrThrow(response, json) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }
    }

    private inner class AsyncXmlCallback<T>(
        private val onSuccess: ((T) -> Unit),
        private val onFailure: ((Throwable) -> Unit)?,
        private val xml: XML,
    ) : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onFailure?.invoke(e)
        }

        override fun onResponse(call: Call, response: Response) {
            runCatching { return@onResponse handleResponseOrThrow(response, xml) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }
    }
}
