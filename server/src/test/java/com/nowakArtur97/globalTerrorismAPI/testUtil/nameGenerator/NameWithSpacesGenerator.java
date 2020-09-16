package com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

public class NameWithSpacesGenerator extends ReplaceUnderscores {

	private final String wordEndingTestName = "should";

	private final String signReplacedInMethodName = "_";

	private final String signReplacingInMethodName = " ";

	@Override
	public String generateDisplayNameForClass(Class<?> testClass) {

		String classMethodName = super.generateDisplayNameForClass(testClass);

		String displayName = addSpacesBetweenWords(classMethodName);

		return displayName;
	}

	@Override
	public String generateDisplayNameForNestedClass(Class<?> nestedClass) {

		String nestedClassMethodName = nestedClass.getSimpleName();

		String displayName = addSpacesBetweenWords(nestedClassMethodName);

		return displayName;
	}

	@Override
	public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {

		String testMethodName = testMethod.getName();

		int indexOfShouldWord = testMethodName.indexOf(wordEndingTestName);

		String displayName = testMethodName.substring(0, indexOfShouldWord - 1).replace(signReplacedInMethodName,
				signReplacingInMethodName);

		return displayName;
	}

	private String addSpacesBetweenWords(String className) {

		int wordLength = className.length();

		StringBuilder result = new StringBuilder();
		result.append(className.charAt(0));

		for (int i = 1; i < wordLength; i++) {

			if (Character.isUpperCase(className.charAt(i))) {

				result.append(' ');
			}

			result.append(className.charAt(i));
		}

		return result.toString();
	}
}
