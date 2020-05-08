package com.NowakArtur97.GlobalTerrorismAPI.util;

public interface ViolationHelper {

	<T> void violate(Object entity, Class<T> dtoType);
}
