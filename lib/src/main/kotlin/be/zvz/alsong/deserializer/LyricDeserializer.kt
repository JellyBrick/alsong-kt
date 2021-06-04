package be.zvz.alsong.deserializer

import be.zvz.alsong.Utils
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class LyricDeserializer : JsonDeserializer<Map<Long, List<String>>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Map<Long, List<String>> =
        Utils.parseLyric(p.valueAsString)
}
