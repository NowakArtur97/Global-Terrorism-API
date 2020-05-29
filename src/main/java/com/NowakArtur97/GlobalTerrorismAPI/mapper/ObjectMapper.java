package com.NowakArtur97.GlobalTerrorismAPI.mapper;

public interface ObjectMapper {

    <T> T map(Object source, Class<T> destinationType);
}
