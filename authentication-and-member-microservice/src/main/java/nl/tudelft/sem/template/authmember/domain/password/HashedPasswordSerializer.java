package nl.tudelft.sem.template.authmember.domain.password;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * A DDD entity representing an application user in our domain.
 */
class HashedPasswordSerializer extends JsonSerializer<HashedPassword> {

    @Override
    public void serialize(HashedPassword value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }
}
