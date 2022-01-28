package be.zvz.alsong

import be.zvz.alsong.dto.LyricInfo
import be.zvz.alsong.dto.LyricLookup
import be.zvz.alsong.dto.LyricMurekaId
import be.zvz.alsong.dto.SearchResult
import be.zvz.alsong.exception.InvalidDataReceivedException
import be.zvz.alsong.exception.NoDataReceivedException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.Result as FuelResult

class Alsong(
    private val mapper: ObjectMapper = JsonMapper()
        .registerKotlinModule()
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE),
    private val fuelManager: FuelManager = FuelManager()
) {
    private var encKey = Utils.getEncKey()

    init {
        fuelManager.basePath = Utils.BASE_URL
        fuelManager.baseHeaders = mapOf(
            "User-Agent" to "android"
        )
    }

    @JvmOverloads
    fun getResembleLyricList(
        artist: String,
        title: String,
        playtime: Int = 0,
        page: Int = 1
    ): List<SearchResult> = handleResponseOrThrow(
        fuelManager.post(
            "/v1/search",
            mutableListOf(
                "title" to title,
                "artist" to artist,
                "page" to page,
                "encData" to encKey
            ).apply {
                if (playtime != 0) {
                    add("playtime" to playtime)
                }
            }
        )
            .responseObject<List<SearchResult>>(mapper = mapper)
            .third
    )

    @JvmOverloads
    fun getResembleLyricList(
        artist: String,
        title: String,
        playtime: Int = 0,
        page: Int = 1,
        onSuccess: (List<SearchResult>) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null
    ) = fuelManager.post(
        "/v1/search",
        mutableListOf(
            "title" to title,
            "artist" to artist,
            "page" to page,
            "encData" to encKey
        ).apply {
            if (playtime != 0) {
                add("playtime" to playtime)
            }
        }
    )
        .responseObject<List<SearchResult>>(mapper = mapper) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    fun getLyricById(
        lyricId: Long
    ): LyricInfo = handleResponseOrThrow(
        fuelManager.post(
            "/v1/info",
            listOf(
                "info_id" to lyricId,
                "encData" to encKey
            )
        )
            .responseObject<LyricInfo>(mapper = mapper)
            .third
    )

    @JvmOverloads
    fun getLyricById(
        lyricId: Long,
        onSuccess: (LyricInfo) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null
    ) = fuelManager.post(
        "/v1/info",
        listOf(
            "info_id" to lyricId,
            "encData" to encKey
        )
    )
        .responseObject<LyricInfo>(mapper = mapper) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    fun getLyricByHash(
        md5: String
    ): LyricLookup = handleResponseOrThrow(
        fuelManager.post(
            "/v1/lookup",
            listOf(
                "md5" to md5,
                "encData" to encKey
            )
        )
            .responseObject<LyricLookup>(mapper = mapper)
            .third
    )

    @JvmOverloads
    fun getLyricByHash(
        md5: String,
        onSuccess: (LyricLookup) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null
    ) = fuelManager.post(
        "/v1/lookup",
        listOf(
            "md5" to md5,
            "encData" to encKey
        )
    )
        .responseObject<LyricLookup>(mapper = mapper) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    fun getLyricByMurekaId(
        murekaId: Long
    ): List<LyricMurekaId> = handleResponseOrThrow(
        fuelManager.post(
            "/v1/lookupListByMurekaId",
            listOf(
                "murekaid" to murekaId,
                "encData" to encKey
            )
        )
            .responseObject<List<LyricMurekaId>>(mapper = mapper)
            .third
    )

    @JvmOverloads
    fun getLyricByMurekaId(
        murekaId: Long,
        onSuccess: (List<LyricMurekaId>) -> Unit,
        onFailure: ((Throwable) -> Unit)? = null
    ) = fuelManager.post(
        "/v1/lookupListByMurekaId",
        listOf(
            "murekaid" to murekaId,
            "encData" to encKey
        )
    )
        .responseObject<List<LyricMurekaId>>(mapper = mapper) { _, _, result ->
            runCatching { handleResponseOrThrow(result) }
                .mapCatching(onSuccess)
                .getOrElse { onFailure?.invoke(it) }
        }

    fun refreshEncKey() {
        encKey = Utils.getEncKey()
    }

    private fun <T> handleResponse(result: FuelResult<T, FuelError>): T =
        result.getOrElse {
            throw InvalidDataReceivedException(it.exception)
        }

    private fun <T> handleResponseOrThrow(result: FuelResult<T, FuelError>): T =
        handleResponse(result) ?: throw NoDataReceivedException()
}
