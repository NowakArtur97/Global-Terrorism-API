package com.NowakArtur97.GlobalTerrorismAPI.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
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
}
