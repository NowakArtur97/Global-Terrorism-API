package com.NowakArtur97.GlobalTerrorismAPI.util.patch;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;

public interface PatchHelper {

	<T> T patch(JsonPatch jsonPatch, T targetBean, Class<T> beanClass);

	<T> T mergePatch(JsonMergePatch jsonMergePatch, T targetBean, Class<T> beanClass);
}
