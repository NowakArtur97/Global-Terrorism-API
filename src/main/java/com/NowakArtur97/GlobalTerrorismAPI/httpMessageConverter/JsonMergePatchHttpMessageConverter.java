package com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonReader;
import javax.json.JsonWriter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

@Component
public class JsonMergePatchHttpMessageConverter extends AbstractHttpMessageConverter<JsonMergePatch> {

	private final static String MEDIA_TYPE = "application/merge-patch+json";

	public JsonMergePatchHttpMessageConverter() {
		super(MediaType.valueOf(MEDIA_TYPE));
	}

	@Override
	protected boolean supports(Class<?> clazz) {

		return JsonMergePatch.class.isAssignableFrom(clazz);
	}

	@Override
	protected JsonMergePatch readInternal(Class<? extends JsonMergePatch> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		try (JsonReader jsonReader = Json.createReader(inputMessage.getBody())) {

			return Json.createMergePatch(jsonReader.readValue());

		} catch (Exception ex) {

			throw new HttpMessageNotReadableException(ex.getMessage(), inputMessage);
		}
	}

	@Override
	protected void writeInternal(JsonMergePatch jsonMergePatch, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		try (JsonWriter jsonWriter = Json.createWriter(outputMessage.getBody())) {

			jsonWriter.write(jsonMergePatch.toJsonValue());

		} catch (Exception ex) {

			throw new HttpMessageNotWritableException(ex.getMessage(), ex);
		}
	}
}
