package com.NowakArtur97.GlobalTerrorismAPI.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ViolationHelperImpl_Tests")
class ViolationHelperImplTest {

	private ViolationHelper violationHelper;

	@Mock
	private Validator validator;

	@Mock
	private DTOMapper dtoMapper;

	@Mock
	@SuppressWarnings("rawtypes")
	private ConstraintViolation constraintViolation;

	private TargetBuilder targetBuilder;
	private EventBuilder eventBuilder;

	@BeforeEach
	private void setUp() {

		violationHelper = new ViolationHelperImpl(validator, dtoMapper);

		targetBuilder = new TargetBuilder();
		eventBuilder = new EventBuilder();
	}

	@Test
	void when_violate_valid_target_should_not_have_violations() {

		Long targetId = 1L;
		String invalidTargetName = "some invalid target name";
		TargetDTO targetDTO = new TargetDTO(invalidTargetName);
		TargetNode targetNode = new TargetNode(targetId, invalidTargetName);

		Set<ConstraintViolation<TargetDTO>> violationsExpected = new HashSet<ConstraintViolation<TargetDTO>>();

		when(dtoMapper.mapToDTO(targetNode, TargetDTO.class)).thenReturn(targetDTO);
		when(validator.validate(targetDTO)).thenReturn(violationsExpected);

		assertAll(
				() -> assertDoesNotThrow(() -> violationHelper.violate(targetNode, TargetDTO.class),
						() -> "should not throw Constraint Violation Exception, but was thrown"),
				() -> verify(dtoMapper, times(1)).mapToDTO(targetNode, TargetDTO.class),
				() -> verifyNoMoreInteractions(dtoMapper), () -> verify(validator, times(1)).validate(targetDTO),
				() -> verifyNoMoreInteractions(validator));
	}

	@Test
	@SuppressWarnings("unchecked")
	void when_violate_invalid_target_should_have_violations() {

		Long targetId = 1L;
		String invalidTargetName = "some invalid target name";
		TargetDTO targetDTO = new TargetDTO(invalidTargetName);
		TargetNode targetNode = new TargetNode(targetId, invalidTargetName);

		Set<ConstraintViolation<TargetDTO>> violationsExpected = new HashSet<ConstraintViolation<TargetDTO>>();

		violationsExpected.add(constraintViolation);

		Class<ConstraintViolationException> expectedException = ConstraintViolationException.class;

		when(dtoMapper.mapToDTO(targetNode, TargetDTO.class)).thenReturn(targetDTO);
		when(validator.validate(targetDTO)).thenReturn(violationsExpected);

		assertAll(
				() -> assertThrows(expectedException, () -> violationHelper.violate(targetNode, TargetDTO.class),
						() -> "should throw Constraint Violation Exception, but nothing was thrown"),
				() -> verify(dtoMapper, times(1)).mapToDTO(targetNode, TargetDTO.class),
				() -> verifyNoMoreInteractions(dtoMapper), () -> verify(validator, times(1)).validate(targetDTO),
				() -> verifyNoMoreInteractions(validator));
	}

	@Test
	void when_violate_valid_event_should_not_have_violations() {

		TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
		EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
		TargetNode targetNode = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);
		EventNode eventNode = (EventNode) eventBuilder.withId(null).withTarget(targetNode).build(ObjectType.NODE);

		Set<ConstraintViolation<EventDTO>> violationsExpected = new HashSet<ConstraintViolation<EventDTO>>();

		when(dtoMapper.mapToDTO(eventNode, EventDTO.class)).thenReturn(eventDTO);
		when(validator.validate(eventDTO)).thenReturn(violationsExpected);

		assertAll(
				() -> assertDoesNotThrow(() -> violationHelper.violate(eventNode, EventDTO.class),
						() -> "should not throw Constraint Violation Exception, but was thrown"),
				() -> verify(dtoMapper, times(1)).mapToDTO(eventNode, EventDTO.class),
				() -> verifyNoMoreInteractions(dtoMapper), () -> verify(validator, times(1)).validate(eventDTO),
				() -> verifyNoMoreInteractions(validator));
	}

	@Test
	@SuppressWarnings("unchecked")
	void when_violate_invalid_event_should_have_violations() {

		TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
		EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
		TargetNode targetNode = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);
		EventNode eventNode = (EventNode) eventBuilder.withId(null).withTarget(targetNode).build(ObjectType.NODE);

		Set<ConstraintViolation<EventDTO>> violationsExpected = new HashSet<ConstraintViolation<EventDTO>>();

		violationsExpected.add(constraintViolation);

		Class<ConstraintViolationException> expectedException = ConstraintViolationException.class;

		when(dtoMapper.mapToDTO(eventNode, EventDTO.class)).thenReturn(eventDTO);
		when(validator.validate(eventDTO)).thenReturn(violationsExpected);

		assertAll(
				() -> assertThrows(expectedException, () -> violationHelper.violate(eventNode, EventDTO.class),
						() -> "should throw Constraint Violation Exception, but nothing was thrown"),
				() -> verify(dtoMapper, times(1)).mapToDTO(eventNode, EventDTO.class),
				() -> verifyNoMoreInteractions(dtoMapper), () -> verify(validator, times(1)).validate(eventDTO),
				() -> verifyNoMoreInteractions(validator));
	}
}
