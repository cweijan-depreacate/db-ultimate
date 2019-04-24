package github.cweijan.ultimate.json.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import github.cweijan.ultimate.util.DateUtils
import java.time.LocalDate

class LocalDateDeserializer : JsonDeserializer<LocalDate>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDate? {

        return DateUtils.toDateObject(p.text.trim(), LocalDate::class.java) as LocalDate?
    }
}
