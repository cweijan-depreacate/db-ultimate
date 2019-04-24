package github.cweijan.ultimate.json.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import github.cweijan.ultimate.util.DateUtils
import java.time.LocalDateTime

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime? {

        return DateUtils.toDateObject(p.text.trim(), LocalDateTime::class.java) as LocalDateTime?
    }
}
