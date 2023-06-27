package be.zvz.alsong.dto

data class UserData @JvmOverloads constructor(
    val firstName: String,
    val firstEmail: String = "",
    val firstUrl: String = "",
    val firstPhone: String = "",
    val firstComment: String = "",
    val name: String = firstName,
    val email: String = firstEmail,
    val url: String = firstUrl,
    val phone: String = firstPhone,
    val comment: String = firstComment,
)
