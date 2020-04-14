package com.NowakArtur97.GlobalTerrorismAPI.util;

import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ViolationHelper_Tests")
public class ViolationHelperTest {

	private ViolationHelper violationHelper;

	@Mock
	private Validator validator;

	@Mock
	private DTOMapper dtoMapper;

	@BeforeEach
	public void setUp() {

		violationHelper = new ViolationHelperImpl(validator, dtoMapper);
	}
}
