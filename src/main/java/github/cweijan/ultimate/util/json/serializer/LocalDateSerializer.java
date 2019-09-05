package github.cweijan.ultimate.util.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import github.cweijan.ultimate.util.DateUtils;
import github.cweijan.ultimate.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author cweijan
 * @version 2019/9/5 11:39
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String dateString = DateUtils.toDateString(localDate);
        if(StringUtils.isNotEmpty(dateString)){
            jsonGenerator.writeString(dateString);
        }
    }
}
