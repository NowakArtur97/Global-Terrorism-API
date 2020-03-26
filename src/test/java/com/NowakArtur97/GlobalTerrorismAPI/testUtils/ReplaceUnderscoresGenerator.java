package com.NowakArtur97.GlobalTerrorismAPI.testUtils;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

public class ReplaceUnderscoresGenerator extends ReplaceUnderscores {

	@Override
	public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {

		String testMethodName = testMethod.getName();

		int indexOfShouldWord = testMethodName.indexOf("should");

		String displayName = testMethodName.substring(0, indexOfShouldWord - 1).replace("_", " ");

		return displayName;
	}
}
