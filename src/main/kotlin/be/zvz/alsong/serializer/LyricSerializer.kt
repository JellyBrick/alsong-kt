package be.zvz.alsong.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LyricSerializer : KSerializer<Map<Long, List<String>>> {
    private val LYRIC_REGEX = Regex("^(.)?\\[(\\d+):(\\d\\d).(\\d\\d)](.*)\$")
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LyricDeserializer", PrimitiveKind.STRING)

    // format: milliseconds to MM:SS.CC
    private fun timestampToTimeString(
        timestamp: Long,
    ) = "%02d:%02d.%02d".format(
        timestamp / 1000 / 60,
        timestamp / 1000 % 60,
        timestamp / 10 % 100,
    )

    override fun deserialize(decoder: Decoder): Map<Long, List<String>> = mutableMapOf<Long, MutableList<String>>().apply {
        decoder.decodeString().split("<br>").forEach {
            LYRIC_REGEX.matchEntire(it)?.let { match ->
                val groupValues = match.groupValues
                val timestamp = 10 * (groupValues[2].toLong() * 60 * 100 + groupValues[3].toLong() * 100 + groupValues[4].toLong())
                if (containsKey(timestamp)) {
                    getValue(timestamp).add(groupValues[5])
                } else {
                    this[timestamp] = mutableListOf(groupValues[5])
                }
            }
        }
    }.toSortedMap()

    override fun serialize(encoder: Encoder, value: Map<Long, List<String>>) {
        encoder.encodeString(
            StringBuilder().apply {
                for ((key, values) in value) {
                    val timestampString = timestampToTimeString(key)
                    for (lyric in values) {
                        append("[")
                        append(timestampString)
                        append("]")
                        append(lyric)
                        append("<br>")
                    }
                }
                if (isNotEmpty()) {
                    setLength(length - "<br>".length)
                }
            }.toString(),
        )
    }
}
