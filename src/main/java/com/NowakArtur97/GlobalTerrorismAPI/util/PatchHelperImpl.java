package com.NowakArtur97.GlobalTerrorismAPI.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;

@Component
@RequiredArgsConstructor
class PatchHelperImpl implements PatchHelper {

    private final ObjectMapper objectMapper;

    @Override
    public <T> T patch(JsonPatch jsonPatch, T targetBean, Class<T> beanClass) {

        JsonStructure target = objectMapper.convertValue(targetBean, JsonStructure.class);

        JsonValue patched = jsonPatch.apply(target);

        return objectMapper.convertValue(patched, beanClass);
    }

    @Override
    public <T> T mergePatch(JsonMergePatch jsonMergePatch, T targetBean, Class<T> beanClass) {

        JsonValue target = objectMapper.convertValue(targetBean, JsonValue.class);

        JsonValue patched = jsonMergePatch.apply(target);

        return objectMapper.convertValue(patched, beanClass);
    }
}
