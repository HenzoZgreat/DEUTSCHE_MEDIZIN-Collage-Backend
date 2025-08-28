package Henok.example.DeutscheCollageBack_endAPI.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Base64;

public class Base64ImageDeserializer extends StdDeserializer<byte[]> {

    public Base64ImageDeserializer() {
        this(null);
    }

    public Base64ImageDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String base64String = p.getText();
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        // Remove prefixes: data:image/[type];base64, or image/[type];base64,
        String cleanBase64 = base64String.replaceAll("^(data:image/[^;]+;base64,|image/[^;]+;base64,)", "");
        try {
            return Base64.getDecoder().decode(cleanBase64);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid Base64 string: " + e.getMessage(), e);
        }
    }
}