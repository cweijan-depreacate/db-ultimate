package github.cweijan.ultimate.util.json.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import github.cweijan.ultimate.util.DateUtils
import java.time.LocalDate

class LocalDateSerializer : JsonSerializer<LocalDate>() {
    override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider?) {
        DateUtils.toDateString(value)?.run { gen.writeString(this) }
    }
}
