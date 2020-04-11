package com.NowakArtur97.GlobalTerrorismAPI.util;

import java.util.Set;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PatchHelper {

	private final ObjectMapper objectMapper;

	private final Validator valdiator;

	public <T> T patch(JsonPatch jsonPatch, T targetBean, Class<T> beanClass) {

		JsonStructure target = objectMapper.convertValue(targetBean, JsonStructure.class);

		JsonValue patched = jsonPatch.apply(target);

		T beanPatched = objectMapper.convertValue(patched, beanClass);

		Set<ConstraintViolation<T>> violations = valdiator.validate(beanPatched);

		if (!violations.isEmpty()) {

			throw new ConstraintViolationException(violations);
		}

		return objectMapper.convertValue(patched, beanClass);
	}

	public <T> T mergePatch(JsonMergePatch jsonMergePatch, T targetBean, Class<T> beanClass) {

		JsonValue target = objectMapper.convertValue(targetBean, JsonValue.class);

		JsonValue patched = jsonMergePatch.apply(target);

		T beanPatched = objectMapper.convertValue(patched, beanClass);

		Set<ConstraintViolation<T>> violations = valdiator.validate(beanPatched);

		if (!violations.isEmpty()) {

			throw new ConstraintViolationException(violations);
		}

		return objectMapper.convertValue(patched, beanClass);
	}
}
