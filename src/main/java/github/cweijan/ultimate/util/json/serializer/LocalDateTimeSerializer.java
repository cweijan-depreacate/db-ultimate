package github.cweijan.ultimate.util.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import github.cweijan.ultimate.util.DateUtils;
import github.cweijan.ultimate.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author cweijan
 * @version 2019/9/5 11:41
 */
public class LocalDateTimeSerializer  extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String dateString = DateUtils.toDateString(localDateTime);
        if(StringUtils.isNotEmpty(dateString)){
            jsonGenerator.writeString(dateString);
        }
    }
}
