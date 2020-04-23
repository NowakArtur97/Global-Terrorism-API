package com.NowakArtur97.GlobalTerrorismAPI.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
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

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.ibm.icu.util.Calendar;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventDTOValidation_Tests")
public class EventDTOValidationTest {

	private Validator validator;

	@BeforeEach
	private void setUp() {

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void when_event_dto_is_valid_should_not_have_violations() {

		String eventSummary = "summary";
		String eventMotive = "motive";
		Date eventDate = Calendar.getInstance().getTime();
		boolean isEventPartOfMultipleIncidents = true;
		boolean isEventSuccessful = true;
		boolean isEventSuicide = true;

		EventDTO eventDTO = EventDTO.builder().date(eventDate).summary(eventSummary).motive(eventMotive)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).build();

		Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

		assertAll(() -> assertTrue(violations.isEmpty(),
				() -> "shouldn`t have violations, but have: " + violations.size()));
	}

	@Test
	void when_event_dto_has_null_fields_should_have_violations() {

		String summary = null;
		String motive = null;
		Date date = null;
		Boolean isPartOfMultipleIncidents = null;
		Boolean isSuccessful = null;
		Boolean isSuicide = null;

		EventDTO eventDTO = EventDTO.builder().date(date).summary(summary).motive(motive)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.build();

		Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

		assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violations, but haven't"),
				() -> assertEquals(6, violations.size(),
						() -> "should have six violations, but have: " + violations.size()));
	}

	@ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void when_event_dto_has_invalid_summary_should_have_violations(String invalidSummary) {

		String motive = "motive";
		Date date = Calendar.getInstance().getTime();
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		EventDTO eventDTO = EventDTO.builder().date(date).summary(invalidSummary).motive(motive)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.build();

		Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

		assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
				() -> assertEquals(1, violations.size(),
						() -> "should have one violation, but have: " + violations.size()));
	}

	@ParameterizedTest(name = "{index}: For motive: {0} should have violation")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void when_event_dto_has_invalid_motive_should_have_violations(String invalidMotive) {

		String summary = "summary";
		Date date = Calendar.getInstance().getTime();
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		EventDTO eventDTO = EventDTO.builder().date(date).summary(summary).motive(invalidMotive)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.build();

		Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

		assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
				() -> assertEquals(1, violations.size(),
						() -> "should have one violation, but have: " + violations.size()));
	}

	@Test
	void when_event_dto_has_date_in_the_future_should_have_violation() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(2090, 1, 1);
		Date invalidDate = calendar.getTime();

		String motive = "motive";
		String summary = "summary";
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		EventDTO eventDTO = EventDTO.builder().date(invalidDate).summary(summary).motive(motive)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.build();

		Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

		assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
				() -> assertEquals(1, violations.size(),
						() -> "should have one violation, but have: " + violations.size()));
	}
}
