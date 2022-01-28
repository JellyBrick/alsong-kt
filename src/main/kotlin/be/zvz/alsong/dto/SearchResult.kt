package be.zvz.alsong.dto

data class SearchResult(
    val lyricId: Long,
    val playtime: Long,
    val artist: String,
    val album: String,
    val title: String,
    val registerDate: String
)
