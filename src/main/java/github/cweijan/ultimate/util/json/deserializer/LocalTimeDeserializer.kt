package github.cweijan.ultimate.util.json.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import github.cweijan.ultimate.util.DateUtils
import java.time.LocalTime

class LocalTimeDeserializer : JsonDeserializer<LocalTime>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalTime? {

        return DateUtils.toDateObject(p.text.trim(), LocalTime::class.java) as LocalTime?
    }
}
