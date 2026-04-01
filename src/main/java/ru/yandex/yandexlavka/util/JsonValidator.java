package ru.yandex.yandexlavka.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Data
public class JsonValidator {
    private String jsonSchemaName;
    private final JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonValidator() {
    }

    public JsonValidator(String jsonSchemaName) {
        this.jsonSchemaName = jsonSchemaName;
    }

    public boolean validateJsonString(String jsonString) {
        ClassPathResource classPathResource = new ClassPathResource(this.jsonSchemaName + ".json");
        JsonNode jsonNodeFromSchema;
        JsonNode jsonNode;
        try {
            InputStream inputStream = classPathResource.getInputStream();
            jsonNodeFromSchema = objectMapper.readTree(inputStream).get(this.jsonSchemaName);
            jsonNode = objectMapper.readTree(jsonString);
        } catch (IOException e) {
            return false;
        }
        JsonSchema jsonSchema = jsonSchemaFactory.getSchema(jsonNodeFromSchema);
        Set<ValidationMessage> validationMessages = jsonSchema.validate(jsonNode);
        if (validationMessages.size() != 0) {
            return false;
        }
        return true;
    }

}
