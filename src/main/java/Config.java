import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Jacob on 2017/7/17.
 */
public class Config {
    public static ObjectMapper objectMapper;
    static {
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        objectMapper = new ObjectMapper(factory);
    }
}
