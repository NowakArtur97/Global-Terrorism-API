package com.NowakArtur97.GlobalTerrorismAPI.controller.target;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonMergePatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonPatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
class TargetControllerPatchMethodTest {

	private final String BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private GenericRestController<TargetModel, TargetDTO> targetController;

	private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

	@Mock
	private GenericService<TargetNode, TargetDTO> targetService;

	@Mock
	private TargetModelAssembler targetModelAssembler;

	@Mock
	private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Mock
	private ViolationHelper<TargetNode, TargetDTO> violationHelper;

	@BeforeEach
	private void setUp() {

		targetController = new TargetController(targetService, targetModelAssembler, pagedResourcesAssembler,
				patchHelper, violationHelper);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(targetController, restResponseGlobalEntityExceptionHandler)
				.setMessageConverters(new JsonMergePatchHttpMessageConverter(), new JsonPatchHttpMessageConverter(),
						new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Test
	void when_partial_update_valid_target_using_json_patch_should_return_partially_updated_node() {

		Long targetId = 1L;
		String oldTargetName = "target";
		String updatedTargetName = "updated target";
		TargetNode targetNode = new TargetNode(targetId, oldTargetName);
		TargetNode targetNodeUpdated = new TargetNode(targetId, updatedTargetName);
		TargetModel targetModel = new TargetModel(targetId, updatedTargetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkWithParameter = BASE_PATH + "/" + "{id}";

		when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
		when(patchHelper.patch(any(JsonPatch.class), eq(targetNode), ArgumentMatchers.<Class<TargetNode>>any()))
				.thenReturn(targetNodeUpdated);
		doNothing().when(violationHelper).violate(targetNodeUpdated, TargetDTO.class);
		when(targetService.save(targetNodeUpdated)).thenReturn(targetNodeUpdated);
		when(targetModelAssembler.toModel(targetNodeUpdated)).thenReturn(targetModel);

		assertAll(
				() -> mockMvc
						.perform(patch(linkWithParameter, targetId).content(
								"[ { \"op\": \"replace\", \"path\": \"/target\", \"value\": \"updated target\" } ]")
								.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToLink)))
						.andExpect(jsonPath("id", is(targetId.intValue())))
						.andExpect(jsonPath("target", is(updatedTargetName)))
						.andExpect(jsonPath("target", not(oldTargetName))),
				() -> verify(targetService, times(1)).findById(targetId),
				() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class), eq(targetNode),
						ArgumentMatchers.<Class<TargetNode>>any()),
				() -> verifyNoMoreInteractions(patchHelper),
				() -> verify(violationHelper, times(1)).violate(targetNodeUpdated, TargetDTO.class),
				() -> verifyNoMoreInteractions(violationHelper),
				() -> verify(targetService, times(1)).save(targetNodeUpdated),
				() -> verifyNoMoreInteractions(targetService),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNodeUpdated),
				() -> verifyNoMoreInteractions(targetModelAssembler),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@Test
	void when_partial_update_valid_target_using_json_merge_patch_should_return_partially_updated_node() {

		Long targetId = 1L;
		String oldTargetName = "target";
		String updatedTargetName = "updated target";
		TargetNode targetNode = new TargetNode(targetId, oldTargetName);
		TargetNode targetNodeUpdated = new TargetNode(targetId, updatedTargetName);
		TargetModel targetModel = new TargetModel(targetId, updatedTargetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkWithParameter = BASE_PATH + "/" + "{id2}";

		when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
		when(patchHelper.mergePatch(any(JsonMergePatch.class), eq(targetNode),
				ArgumentMatchers.<Class<TargetNode>>any())).thenReturn(targetNodeUpdated);
		doNothing().when(violationHelper).violate(targetNodeUpdated, TargetDTO.class);
		when(targetService.save(targetNodeUpdated)).thenReturn(targetNodeUpdated);
		when(targetModelAssembler.toModel(targetNodeUpdated)).thenReturn(targetModel);

		assertAll(
				() -> mockMvc
						.perform(patch(linkWithParameter, targetId).content("{ \"target\": \"updated target\" }")
								.contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToLink)))
						.andExpect(jsonPath("id", is(targetId.intValue())))
						.andExpect(jsonPath("target", is(updatedTargetName)))
						.andExpect(jsonPath("target", not(oldTargetName))),
				() -> verify(targetService, times(1)).findById(targetId),
				() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class), eq(targetNode),
						ArgumentMatchers.<Class<TargetNode>>any()),
				() -> verifyNoMoreInteractions(patchHelper),
				() -> verify(violationHelper, times(1)).violate(targetNodeUpdated, TargetDTO.class),
				() -> verifyNoMoreInteractions(violationHelper),
				() -> verify(targetService, times(1)).save(targetNodeUpdated),
				() -> verifyNoMoreInteractions(targetService),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNodeUpdated),
				() -> verifyNoMoreInteractions(targetModelAssembler),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}
}