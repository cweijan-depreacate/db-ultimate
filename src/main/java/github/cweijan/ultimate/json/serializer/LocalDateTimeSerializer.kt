package github.cweijan.ultimate.json.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import github.cweijan.ultimate.util.DateUtils
import java.time.LocalDateTime

class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider?) {
        DateUtils.toDateString(value)?.run { gen.writeString(this) }
    }
}
