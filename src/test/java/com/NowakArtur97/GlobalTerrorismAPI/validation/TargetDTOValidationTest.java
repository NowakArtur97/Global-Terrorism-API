package com.NowakArtur97.GlobalTerrorismAPI.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetDTOValidation_Tests")
class TargetDTOValidationTest {

	private Validator validator;

	@BeforeEach
	private void setUp() {

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void when_target_dto_is_valid_should_not_have_violations() {

		String targetName = "Target";

		TargetDTO targetDTO = new TargetDTO(targetName);

		Set<ConstraintViolation<TargetDTO>> violations = validator.validate(targetDTO);

		assertAll(() -> assertTrue(violations.isEmpty(),
				() -> "shouldn`t have violations, but have: " + violations.size()));
	}

	@ParameterizedTest(name = "{index}: Target Name: {0}")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void when_target_dto_is_invalid_should_have_violations(String targetName) {

		TargetDTO targetDTO = new TargetDTO(targetName);

		Set<ConstraintViolation<TargetDTO>> violations = validator.validate(targetDTO);

		assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"), () -> assertEquals(1,
				violations.size(), () -> "should have one violation, but have: " + violations.size()));
	}
}
