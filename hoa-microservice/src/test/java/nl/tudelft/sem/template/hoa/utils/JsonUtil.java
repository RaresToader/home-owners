package nl.tudelft.sem.template.hoa.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * The Json util for tests.
 */
public class JsonUtil {
    /**
     * Serialize object into a string.
     *
     * @param object The object to be serialized.
     * @return A serialized string.
     * @throws JsonProcessingException if an error occurs during serialization.
     */
    public static String serialize(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String string = objectMapper.writeValueAsString(object);
        System.out.println(string);
        return string;
    }

    /**
     * Deserializes a json string into an object.
     *
     * @param json The string to be deserialized.
     * @param type The type of the desired object.
     * @return The deserialized object.
     * @throws JsonProcessingException if an error occurs during deserialization.
     */
    public static <T> T deserialize(String json, Class<T> type) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(json, type);
    }
}