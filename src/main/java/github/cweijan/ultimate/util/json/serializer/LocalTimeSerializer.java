package github.cweijan.ultimate.util.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import github.cweijan.ultimate.util.DateUtils;
import github.cweijan.ultimate.util.StringUtils;

import java.io.IOException;
import java.time.LocalTime;

/**
 * @author cweijan
 * @version 2019/9/5 11:42
 */
public class LocalTimeSerializer extends JsonSerializer<LocalTime> {
    @Override
    public void serialize(LocalTime localTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String dateString = DateUtils.toDateString(localTime);
        if (StringUtils.isNotEmpty(dateString)) {
            jsonGenerator.writeString(dateString);
        }
    }
}
