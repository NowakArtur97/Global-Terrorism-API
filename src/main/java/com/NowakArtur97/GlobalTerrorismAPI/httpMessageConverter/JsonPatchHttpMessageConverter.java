package com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter;

import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonPatch;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.IOException;

@Component
public class JsonPatchHttpMessageConverter extends AbstractHttpMessageConverter<JsonPatch> {

    public JsonPatchHttpMessageConverter() {
        super(MediaType.valueOf(PatchMediaType.APPLICATION_JSON_PATCH_VALUE));
    }

    @Override
    protected boolean supports(Class<?> clazz) {

        return JsonPatch.class.isAssignableFrom(clazz);
    }

    @Override
    protected JsonPatch readInternal(Class<? extends JsonPatch> clazz, HttpInputMessage inputMessage)
            throws HttpMessageNotReadableException {

        try (JsonReader jsonReader = Json.createReader(inputMessage.getBody())) {

            return Json.createPatch(jsonReader.readArray());

        } catch (Exception ex) {

            throw new HttpMessageNotReadableException(ex.getMessage(), inputMessage);
        }
    }

    @Override
    protected void writeInternal(JsonPatch jsonPatch, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        try (JsonWriter jsonWriter = Json.createWriter(outputMessage.getBody())) {

            jsonWriter.write(jsonPatch.toJsonArray());

        } catch (Exception ex) {

            throw new HttpMessageNotWritableException(ex.getMessage(), ex);
        }
    }
}
