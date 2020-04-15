package com.NowakArtur97.GlobalTerrorismAPI.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ViolationHelperImpl_Tests")
public class ViolationHelperImplTest {

	private ViolationHelper violationHelper;

	@Mock
	private Validator validator;

	@Mock
	private DTOMapper dtoMapper;

	@Mock
	@SuppressWarnings("rawtypes")
	private ConstraintViolation constraintViolation;

	@BeforeEach
	public void setUp() {

		violationHelper = new ViolationHelperImpl(validator, dtoMapper);
	}

	@Test
	public void when_violate_valid_target_should_not_have_violations() {

		Long targetId = 1L;
		String targetName = "some invalid target name";
		TargetDTO targetDTO = new TargetDTO(targetName);
		TargetNode targetNode = new TargetNode(targetId, targetName);

		Set<ConstraintViolation<TargetDTO>> violationsExpected = new HashSet<ConstraintViolation<TargetDTO>>();

		when(dtoMapper.mapToDTO(targetNode, TargetDTO.class)).thenReturn(targetDTO);
		when(validator.validate(targetDTO)).thenReturn(violationsExpected);

		assertAll(() -> assertDoesNotThrow(() -> violationHelper.violate(targetNode, TargetDTO.class),
				() -> "should not throw Constraint Violation Exception, but was thrown"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void when_violate_invalid_target_should_have_violations() {

		Long targetId = 1L;
		String targetName = "some invalid target name";
		TargetDTO targetDTO = new TargetDTO(targetName);
		TargetNode targetNode = new TargetNode(targetId, targetName);

		Set<ConstraintViolation<TargetDTO>> violationsExpected = new HashSet<ConstraintViolation<TargetDTO>>();

		violationsExpected.add(constraintViolation);

		Class<ConstraintViolationException> expectedException = ConstraintViolationException.class;

		when(dtoMapper.mapToDTO(targetNode, TargetDTO.class)).thenReturn(targetDTO);
		when(validator.validate(targetDTO)).thenReturn(violationsExpected);

		assertAll(() -> assertThrows(expectedException, () -> violationHelper.violate(targetNode, TargetDTO.class),
				() -> "should throw Constraint Violation Exception, but nothing was thrown"));
	}
}
