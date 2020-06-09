package com.NowakArtur97.GlobalTerrorismAPI.validation;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.ibm.icu.util.Calendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupDTOValidation_Tests")
class GroupDTOValidationTest {

    private Validator validator;

    private TargetBuilder targetBuilder;
    private EventBuilder eventBuilder;
    private GroupBuilder groupBuilder;

    @BeforeEach
    private void setUp() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
    }

    @Test
    void when_group_dto_is_valid_should_not_have_violations() {

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violations = validator.validate(groupDTO);

        assertAll(() -> assertTrue(violations.isEmpty(),
                () -> "shouldn`t have violations, but have: " + violations.size()));
    }

    @Test
    void when_group_dto_has_null_fields_should_have_violations() {

        int expectedNumberOfViolations = 2;

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(null).withEventsCaused(null).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violations = validator.validate(groupDTO);

        assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
                () -> assertEquals(expectedNumberOfViolations, violations.size(), () -> "should have: "
                        + expectedNumberOfViolations + " violation, but have: " + violations.size()));
    }

    @Test
    void when_group_dto_has_empty_list_of_events_should_have_violations() {

        int expectedNumberOfViolations = 1;

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(new ArrayList<>()).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violations = validator.validate(groupDTO);

        assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
                () -> assertEquals(expectedNumberOfViolations, violations.size(), () -> "should have: "
                        + expectedNumberOfViolations + " violation, but have: " + violations.size()));
    }

    @ParameterizedTest(name = "{index}: For Group name: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_group_dto_has_invalid_name_should_have_violations(String invalidName) {

        int expectedNumberOfViolations = 1;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(invalidName).withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violations = validator.validate(groupDTO);

        assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
                () -> assertEquals(expectedNumberOfViolations, violations.size(), () -> "should have: "
                        + expectedNumberOfViolations + " violation, but have: " + violations.size()));
    }

    @Test
    void when_group_event_dto_has_null_fields_should_have_violations() {

        int expectedNumberOfViolations = 7;

        EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withSummary(null).withMotive(null).withDate(null)
                .withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicide(null).withTarget(null)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violations = validator.validate(groupDTO);

        assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
                () -> assertEquals(expectedNumberOfViolations, violations.size(), () -> "should have: "
                        + expectedNumberOfViolations + " violation, but have: " + violations.size()));
    }

    @ParameterizedTest(name = "{index}: For Group target: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_group_event_dto_has_invalid_target_should_have_violations(String invalidTarget) {

        int expectedNumberOfViolations = 1;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTarget).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violations = validator.validate(groupDTO);

        assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
                () -> assertEquals(expectedNumberOfViolations, violations.size(), () -> "should have: "
                        + expectedNumberOfViolations + " violation, but have: " + violations.size()));
    }

    @ParameterizedTest(name = "{index}: For Group event summary: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_group_event_dto_has_invalid_summary_should_have_violations(String invalidSummary) {

        int expectedNumberOfViolations = 1;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(invalidSummary).withTarget(targetDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violations = validator.validate(groupDTO);

        assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
                () -> assertEquals(expectedNumberOfViolations, violations.size(), () -> "should have: "
                        + expectedNumberOfViolations + " violation, but have: " + violations.size()));
    }

    @ParameterizedTest(name = "{index}: For Group event motive: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_group_event_dto_has_invalid_motive_should_have_violations(String invalidMotive) {

        int expectedNumberOfViolations = 1;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withMotive(invalidMotive).withTarget(targetDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violations = validator.validate(groupDTO);

        assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
                () -> assertEquals(expectedNumberOfViolations, violations.size(), () -> "should have: "
                        + expectedNumberOfViolations + " violation, but have: " + violations.size()));
    }

    @Test
    void when_group_event_dto_has_date_in_the_future_should_have_violation() {

        int expectedNumberOfViolations = 1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(2090, 1, 1);
        Date invalidDate = calendar.getTime();
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withDate(invalidDate).withTarget(targetDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        Set<ConstraintViolation<GroupDTO>> violations = validator.validate(groupDTO);

        assertAll(() -> assertFalse(violations.isEmpty(), () -> "should have violation, but haven't"),
                () -> assertEquals(expectedNumberOfViolations, violations.size(), () -> "should have: "
                        + expectedNumberOfViolations + " violation, but have: " + violations.size()));
    }
}