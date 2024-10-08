package be.zvz.alsong.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object DateSerializer : KSerializer<Date> {
    private val dateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DateSerializer", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Date,
    ) {
        encoder.encodeString(dateFormat.format(value.time))
    }

    override fun deserialize(decoder: Decoder): Date = dateFormat.parse(decoder.decodeString())
}
