package nl.tudelft.sem.template.authmember.domain.password;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import static org.assertj.core.api.Assertions.assertThat;

class HashedPasswordSerializerTest {

    @Test
    void serialize() throws IOException {
        Writer jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
        new HashedPasswordSerializer().serialize(new HashedPassword("password"), jsonGenerator, serializerProvider);
        jsonGenerator.flush();
        assertThat(jsonWriter.toString()).isEqualTo(("\"password\""));
        jsonGenerator.close();
    }
}