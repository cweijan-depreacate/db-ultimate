package github.cweijan.ultimate.util.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import github.cweijan.ultimate.util.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author cweijan
 * @version 2019/9/5 11:34
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return (LocalDateTime) DateUtils.toDateObject(jsonParser.getText().trim(), LocalDateTime.class);
    }
}
