package com.nowakArtur97.globalTerrorismAPI.common.annotation;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
		@ApiImplicitParam(value = "Results page to retrieve (0..N)", name = "page", dataType = "integer", paramType = "query", defaultValue = "0"),
		@ApiImplicitParam(value = "Number of records per page", name = "size", dataType = "integer", paramType = "query", defaultValue = "100") })
public @interface ApiPageable {

}