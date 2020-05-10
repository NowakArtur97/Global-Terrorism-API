package com.NowakArtur97.GlobalTerrorismAPI.controller.target;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
class TargetControllerPutMethodTest {

	private final String BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private TargetController targetController;

	private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

	@Mock
	private TargetService targetService;

	@Mock
	private TargetModelAssembler targetModelAssembler;

	@Mock
	private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Mock
	private ViolationHelper violationHelper;

	@BeforeEach
	private void setUp() {

		targetController = new TargetController(targetService, targetModelAssembler, pagedResourcesAssembler,
				patchHelper, violationHelper);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(targetController, restResponseGlobalEntityExceptionHandler).build();
	}

	@Test
	void when_update_valid_target_should_return_updated_target_as_model() {

		Long targetId = 1L;
		String oldTargetName = "target";
		String updatedTargetName = "updated target";
		TargetDTO targetDTO = new TargetDTO(oldTargetName);
		TargetNode targetNode = new TargetNode(targetId, updatedTargetName);
		TargetModel targetModel = new TargetModel(targetId, updatedTargetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkWithParameter = BASE_PATH + "/" + "{id}";

		when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
		when(targetService.update(targetId, targetDTO)).thenReturn(targetNode);
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, targetId).content(asJsonString(targetDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToLink)))
						.andExpect(jsonPath("id", is(targetId.intValue())))
						.andExpect(jsonPath("target", is(updatedTargetName)))
						.andExpect(jsonPath("target", not(oldTargetName))),
				() -> verify(targetService, times(1)).findById(targetId),
				() -> verify(targetService, times(1)).update(targetId, targetDTO),
				() -> verifyNoMoreInteractions(targetService),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNode),
				() -> verifyNoMoreInteractions(targetModelAssembler),
				() -> verifyNoInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper));
	}

	@Test
	void when_update_valid_target_with_not_existing_id_should_return_new_target_as_model() {

		Long targetId = 1L;
		String targetName = "target";
		TargetDTO targetDTO = new TargetDTO(targetName);
		TargetNode targetNode = new TargetNode(targetId, targetName);
		TargetModel targetModel = new TargetModel(targetId, targetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkWithParameter = BASE_PATH + "/" + "{id}";

		when(targetService.findById(targetId)).thenReturn(Optional.empty());
		when(targetService.saveNew(targetDTO)).thenReturn(targetNode);
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, targetId).content(asJsonString(targetDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToLink)))
						.andExpect(jsonPath("id", is(targetId.intValue())))
						.andExpect(jsonPath("target", is(targetName))),
				() -> verify(targetService, times(1)).findById(targetId),
				() -> verify(targetService, times(1)).saveNew(targetDTO), () -> verifyNoMoreInteractions(targetService),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNode),
				() -> verifyNoMoreInteractions(targetModelAssembler),
				() -> verifyNoInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper));
	}

	@ParameterizedTest(name = "{index}: Target Name: {0}")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void when_update_invalid_target_should_return_errors(String targetName) {

		Long targetId = 1L;

		TargetDTO targetDTO = new TargetDTO(targetName);

		String linkWithParameter = BASE_PATH + "/" + "{id}";

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, targetId).content(asJsonString(targetDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors[0]", is("{target.target.notBlank}"))),
				() -> verifyNoInteractions(targetService), () -> verifyNoInteractions(targetModelAssembler),
				() -> verifyNoInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper));
	}

	public static String asJsonString(final Object obj) {

		try {

			return new ObjectMapper().writeValueAsString(obj);

		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}
}