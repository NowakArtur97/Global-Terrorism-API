package com.NowakArtur97.GlobalTerrorismAPI.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.advice.TargetControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtils.NameWithSpacesGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
public class TargetControllerTest {

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

	@BeforeEach
	public void setUp() {

		targetController = new TargetController(targetService, targetModelAssembler, pagedResourcesAssembler);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(targetController, restResponseGlobalEntityExceptionHandler)
				.setControllerAdvice(new TargetControllerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
	}

	@Test
	public void when_find_all_targets_with_default_parameters_in_link_and_targets_exist_should_return_all_targets() {

		Long targetId1 = 1L;
		String targetName1 = "target1";
		TargetNode targetNode1 = new TargetNode(targetId1, targetName1);
		TargetModel targetModel1 = new TargetModel(targetId1, targetName1);

		String pathToLink1 = BASE_PATH + targetId1.intValue();
		Link link1 = new Link(pathToLink1);
		targetModel1.add(link1);

		Long targetId2 = 2L;
		String targetName2 = "target2";
		TargetNode targetNode2 = new TargetNode(targetId2, targetName2);
		TargetModel targetModel2 = new TargetModel(targetId2, targetName2);

		String pathToLink2 = BASE_PATH + targetId2.intValue();
		Link link2 = new Link(pathToLink2);
		targetModel2.add(link2);

		Long targetId3 = 3L;
		String targetName3 = "target3";
		TargetNode targetNode3 = new TargetNode(targetId3, targetName3);
		TargetModel targetModel3 = new TargetModel(targetId3, targetName3);

		String pathToLink3 = BASE_PATH + targetId3.intValue();
		Link link3 = new Link(pathToLink3);
		targetModel3.add(link3);

		Long targetId4 = 4L;
		String targetName4 = "target4";
		TargetNode targetNode4 = new TargetNode(targetId4, targetName4);
		TargetModel targetModel4 = new TargetModel(targetId4, targetName4);

		String pathToLink4 = BASE_PATH + targetId4.intValue();
		Link link4 = new Link(pathToLink4);
		targetModel4.add(link4);

		List<TargetNode> targetsListExpected = new ArrayList<>();
		targetsListExpected.add(targetNode1);
		targetsListExpected.add(targetNode2);
		targetsListExpected.add(targetNode3);
		targetsListExpected.add(targetNode4);

		List<TargetModel> targetModelsListExpected = new ArrayList<>();
		targetModelsListExpected.add(targetModel1);
		targetModelsListExpected.add(targetModel2);
		targetModelsListExpected.add(targetModel3);
		targetModelsListExpected.add(targetModel4);

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		int sizeExpected = 100;
		int totalElementsExpected = 4;
		int totalPagesExpected = 1;
		int numberExpected = 0;
		int pageExpected = 0;
		int lastPageExpected = 0;

		Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

		String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
		String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
		String firstPageLink = BASE_PATH + urlParameters1;
		String lastPageLink = BASE_PATH + urlParameters2;

		Link pageLink1 = new Link(firstPageLink, "first");
		Link pageLink2 = new Link(firstPageLink, "self");
		Link pageLink3 = new Link(lastPageLink, "next");
		Link pageLink4 = new Link(lastPageLink, "last");

		PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
		PagedModel<TargetModel> resources = new PagedModel<>(targetModelsListExpected, metadata, pageLink1, pageLink2,
				pageLink3, pageLink4);

		when(targetService.findAll(pageable)).thenReturn(targetsExpected);
		when(pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler)).thenReturn(resources);

		assertAll(
				() -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(firstPageLink)))
						.andExpect(jsonPath("links[1].href", is(firstPageLink)))
						.andExpect(jsonPath("links[2].href", is(lastPageLink)))
						.andExpect(jsonPath("links[3].href", is(lastPageLink)))
						.andExpect(jsonPath("content[0].id", is(targetId1.intValue())))
						.andExpect(jsonPath("content[0].target", is(targetName1)))
						.andExpect(jsonPath("content[0].links[0].href", is(link1.getHref())))
						.andExpect(jsonPath("content[1].id", is(targetId2.intValue())))
						.andExpect(jsonPath("content[1].target", is(targetName2)))
						.andExpect(jsonPath("content[1].links[0].href", is(link2.getHref())))
						.andExpect(jsonPath("content[2].id", is(targetId3.intValue())))
						.andExpect(jsonPath("content[2].target", is(targetName3)))
						.andExpect(jsonPath("content[2].links[0].href", is(link3.getHref())))
						.andExpect(jsonPath("page.size", is(sizeExpected)))
						.andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
						.andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
						.andExpect(jsonPath("page.number", is(numberExpected))),
				() -> verify(targetService, times(1)).findAll(pageable));
	}

	@Test
	public void when_find_all_targets_with_changed_parameters_in_link_and_targets_exist_should_return_all_targets() {

		Long targetId1 = 1L;
		String targetName1 = "target1";
		TargetNode targetNode1 = new TargetNode(targetId1, targetName1);
		TargetModel targetModel1 = new TargetModel(targetId1, targetName1);

		String pathToLink1 = BASE_PATH + targetId1.intValue();
		Link link1 = new Link(pathToLink1);
		targetModel1.add(link1);

		Long targetId2 = 2L;
		String targetName2 = "target2";
		TargetNode targetNode2 = new TargetNode(targetId2, targetName2);
		TargetModel targetModel2 = new TargetModel(targetId2, targetName2);

		String pathToLink2 = BASE_PATH + targetId2.intValue();
		Link link2 = new Link(pathToLink2);
		targetModel2.add(link2);

		Long targetId3 = 3L;
		String targetName3 = "target3";
		TargetNode targetNode3 = new TargetNode(targetId3, targetName3);
		TargetModel targetModel3 = new TargetModel(targetId3, targetName3);

		String pathToLink3 = BASE_PATH + targetId3.intValue();
		Link link3 = new Link(pathToLink3);
		targetModel3.add(link3);

		Long targetId4 = 4L;
		String targetName4 = "target4";
		TargetNode targetNode4 = new TargetNode(targetId4, targetName4);
		TargetModel targetModel4 = new TargetModel(targetId4, targetName4);

		String pathToLink4 = BASE_PATH + targetId4.intValue();
		Link link4 = new Link(pathToLink4);
		targetModel4.add(link4);

		List<TargetNode> targetsListExpected = new ArrayList<>();
		targetsListExpected.add(targetNode1);
		targetsListExpected.add(targetNode2);
		targetsListExpected.add(targetNode3);
		targetsListExpected.add(targetNode4);

		List<TargetModel> targetModelsListExpected = new ArrayList<>();
		targetModelsListExpected.add(targetModel1);
		targetModelsListExpected.add(targetModel2);
		targetModelsListExpected.add(targetModel3);
		targetModelsListExpected.add(targetModel4);

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		int sizeExpected = 3;
		int totalElementsExpected = 4;
		int totalPagesExpected = 2;
		int numberExpected = 0;
		int pageExpected = 0;
		int lastPageExpected = 1;

		Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

		String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
		String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
		String firstPageLink = BASE_PATH + urlParameters1;
		String lastPageLink = BASE_PATH + urlParameters2;

		Link pageLink1 = new Link(firstPageLink, "first");
		Link pageLink2 = new Link(firstPageLink, "self");
		Link pageLink3 = new Link(lastPageLink, "next");
		Link pageLink4 = new Link(lastPageLink, "last");

		PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
		PagedModel<TargetModel> resources = new PagedModel<>(targetModelsListExpected, metadata, pageLink1, pageLink2,
				pageLink3, pageLink4);

		when(targetService.findAll(pageable)).thenReturn(targetsExpected);
		when(pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler)).thenReturn(resources);

		assertAll(
				() -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(firstPageLink)))
						.andExpect(jsonPath("links[1].href", is(firstPageLink)))
						.andExpect(jsonPath("links[2].href", is(lastPageLink)))
						.andExpect(jsonPath("links[3].href", is(lastPageLink)))
						.andExpect(jsonPath("content[0].id", is(targetId1.intValue())))
						.andExpect(jsonPath("content[0].target", is(targetName1)))
						.andExpect(jsonPath("content[0].links[0].href", is(link1.getHref())))
						.andExpect(jsonPath("content[1].id", is(targetId2.intValue())))
						.andExpect(jsonPath("content[1].target", is(targetName2)))
						.andExpect(jsonPath("content[1].links[0].href", is(link2.getHref())))
						.andExpect(jsonPath("content[2].id", is(targetId3.intValue())))
						.andExpect(jsonPath("content[2].target", is(targetName3)))
						.andExpect(jsonPath("content[2].links[0].href", is(link3.getHref())))
						.andExpect(jsonPath("page.size", is(sizeExpected)))
						.andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
						.andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
						.andExpect(jsonPath("page.number", is(numberExpected))),
				() -> verify(targetService, times(1)).findAll(pageable),
				() -> verify(pagedResourcesAssembler, times(1)).toModel(targetsExpected, targetModelAssembler));
	}

	@Test
	public void when_find_all_targets_but_targets_not_exist_should_return_empty_list() {

		List<TargetNode> targetsListExpected = new ArrayList<>();

		List<TargetModel> targetModelsListExpected = new ArrayList<>();

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		int sizeExpected = 100;
		int totalElementsExpected = 0;
		int totalPagesExpected = 0;
		int numberExpected = 0;
		int pageExpected = 0;
		int lastPageExpected = 0;

		Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

		String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
		String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
		String firstPageLink = BASE_PATH + urlParameters1;
		String lastPageLink = BASE_PATH + urlParameters2;

		Link pageLink1 = new Link(firstPageLink, "first");
		Link pageLink2 = new Link(firstPageLink, "self");
		Link pageLink3 = new Link(lastPageLink, "next");
		Link pageLink4 = new Link(lastPageLink, "last");

		PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
		PagedModel<TargetModel> resources = new PagedModel<>(targetModelsListExpected, metadata, pageLink1, pageLink2,
				pageLink3, pageLink4);

		when(targetService.findAll(pageable)).thenReturn(targetsExpected);
		when(pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler)).thenReturn(resources);

		assertAll(
				() -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(firstPageLink)))
						.andExpect(jsonPath("links[1].href", is(firstPageLink)))
						.andExpect(jsonPath("links[2].href", is(lastPageLink)))
						.andExpect(jsonPath("links[3].href", is(lastPageLink))).andExpect(jsonPath("content").isEmpty())
						.andExpect(jsonPath("page.size", is(sizeExpected)))
						.andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
						.andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
						.andExpect(jsonPath("page.number", is(numberExpected))),
				() -> verify(targetService, times(1)).findAll(pageable),
				() -> verify(pagedResourcesAssembler, times(1)).toModel(targetsExpected, targetModelAssembler));
	}

	@Test
	public void when_find_existing_target_should_return_target() {

		Long targetId = 1L;
		String targetName = "target";
		TargetNode targetNode = new TargetNode(targetId, targetName);
		TargetModel targetModel = new TargetModel(targetId, targetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkWithParameter = BASE_PATH + "/" + "{id}";
		String linkExpected = BASE_PATH + "/" + targetId;

		when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(() -> mockMvc.perform(get(linkWithParameter, targetId)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("links[0].href", is(linkExpected)))
				.andExpect(jsonPath("id", is(targetId.intValue()))).andExpect(jsonPath("target", is(targetName))),
				() -> verify(targetService, times(1)).findById(targetId),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNode));
	}

	@Test
	public void when_find_target_but_target_not_exists_should_return_error_response() {

		Long targetId = 1L;

		String linkWithParameter = BASE_PATH + "/" + "{id}";

		when(targetService.findById(targetId)).thenReturn(Optional.empty());

		assertAll(
				() -> mockMvc.perform(get(linkWithParameter, targetId)).andExpect(status().isNotFound())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("timestamp",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()))))
						.andExpect(content().json("{'status': 404}"))
						.andExpect(jsonPath("errors[0]", is("Could not find target with id: " + targetId))),
				() -> verify(targetService, times(1)).findById(targetId),
				() -> verifyNoInteractions(targetModelAssembler));
	}

	@Test
	public void when_add_valid_target_should_return_new_target_as_model() {

		Long targetId = 1L;
		String targetName = "target";
		TargetDTO targetDTO = new TargetDTO(null, targetName);
		TargetNode targetNode = new TargetNode(targetId, targetName);
		TargetModel targetModel = new TargetModel(targetId, targetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkExpected = BASE_PATH + "/" + targetId;

		when(targetService.saveOrUpdate(targetDTO)).thenReturn(targetNode);
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(
				() -> mockMvc
						.perform(post(BASE_PATH).content(asJsonString(targetDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(linkExpected)))
						.andExpect(jsonPath("id", is(targetId.intValue())))
						.andExpect(jsonPath("target", is(targetName))),
				() -> verify(targetService, times(1)).saveOrUpdate(targetDTO),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNode));
	}

	@ParameterizedTest(name = "{index}: Target Name: {0}")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	public void when_add_invalid_target_should_return_errors(String targetName) {

		TargetDTO targetDTO = new TargetDTO(null, targetName);

		assertAll(
				() -> mockMvc
						.perform(post(BASE_PATH).content(asJsonString(targetDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors[0]", is("{target.target.notBlank}"))),
				() -> verifyNoInteractions(targetService), () -> verifyNoInteractions(targetModelAssembler));
	}

	@Test
	public void when_update_valid_target_should_return_updated_target_as_model() {

		Long targetId = 1L;
		String oldTargetName = "target";
		String updatedTargetName = "updated target";
		TargetDTO targetDTO = new TargetDTO(targetId, oldTargetName);
		TargetNode targetNode = new TargetNode(targetId, updatedTargetName);
		TargetModel targetModel = new TargetModel(targetId, updatedTargetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkExpected = BASE_PATH + "/" + targetId;

		when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
		when(targetService.saveOrUpdate(targetDTO)).thenReturn(targetNode);
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(
				() -> mockMvc
						.perform(put(BASE_PATH).content(asJsonString(targetDTO)).contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(linkExpected)))
						.andExpect(jsonPath("id", is(targetId.intValue())))
						.andExpect(jsonPath("target", is(updatedTargetName)))
						.andExpect(jsonPath("target", not(oldTargetName))),
				() -> verify(targetService, times(1)).findById(targetId),
				() -> verify(targetService, times(1)).saveOrUpdate(targetDTO),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNode));
	}

	@Test
	public void when_update_valid_target_without_id_should_return_new_target_as_model() {

		Long targetId = 1L;
		String targetName = "target";
		TargetDTO targetDTO = new TargetDTO(null, targetName);
		TargetNode targetNode = new TargetNode(targetId, targetName);
		TargetModel targetModel = new TargetModel(targetId, targetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkExpected = BASE_PATH + "/" + targetId;

		when(targetService.saveOrUpdate(targetDTO)).thenReturn(targetNode);
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(
				() -> mockMvc
						.perform(put(BASE_PATH).content(asJsonString(targetDTO)).contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(linkExpected)))
						.andExpect(jsonPath("id", is(targetId.intValue())))
						.andExpect(jsonPath("target", is(targetName))),
				() -> verify(targetService, never()).findById(targetId),
				() -> verify(targetService, times(1)).saveOrUpdate(targetDTO),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNode));
	}

	@Test
	public void when_update_valid_target_with_not_existing_id_should_return_new_target_as_model() {

		Long targetId = 1L;
		String targetName = "target";
		TargetDTO targetDTO = new TargetDTO(targetId, targetName);
		TargetDTO targetDTOAfterSet = new TargetDTO(null, targetName);
		TargetNode targetNode = new TargetNode(targetId, targetName);
		TargetModel targetModel = new TargetModel(targetId, targetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkExpected = BASE_PATH + "/" + targetId;

		when(targetService.findById(targetId)).thenReturn(Optional.empty());
		when(targetService.saveOrUpdate(targetDTOAfterSet)).thenReturn(targetNode);
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(
				() -> mockMvc
						.perform(put(BASE_PATH).content(asJsonString(targetDTO)).contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(linkExpected)))
						.andExpect(jsonPath("id", is(targetId.intValue())))
						.andExpect(jsonPath("target", is(targetName))),
				() -> verify(targetService, times(1)).findById(targetId),
				() -> verify(targetService, times(1)).saveOrUpdate(targetDTOAfterSet),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNode));
	}

	@ParameterizedTest(name = "{index}: Target Name: {0}")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	public void when_update_invalid_target_should_return_errors(String targetName) {

		TargetDTO targetDTO = new TargetDTO(null, targetName);

		assertAll(
				() -> mockMvc
						.perform(put(BASE_PATH).content(asJsonString(targetDTO)).contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors[0]", is("{target.target.notBlank}"))),
				() -> verifyNoInteractions(targetService), () -> verifyNoInteractions(targetModelAssembler));
	}

	@Test
	public void when_delete_existing_target_should_return_target() {

		Long targetId = 1L;
		String targetName = "target";
		TargetNode targetNode = new TargetNode(targetId, targetName);
		TargetModel targetModel = new TargetModel(targetId, targetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkWithParameter = BASE_PATH + "/" + "{id}";
		String linkExpected = BASE_PATH + "/" + targetId;

		when(targetService.delete(targetId)).thenReturn(Optional.of(targetNode));
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(() -> mockMvc.perform(delete(linkWithParameter, targetId)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("links[0].href", is(linkExpected)))
				.andExpect(jsonPath("id", is(targetId.intValue()))).andExpect(jsonPath("target", is(targetName))),
				() -> verify(targetService, times(1)).delete(targetId),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNode));
	}

	@Test
	public void when_delete_target_but_target_not_exists_should_return_error_response() {

		Long targetId = 1L;

		String linkWithParameter = BASE_PATH + "/" + "{id}";

		when(targetService.delete(targetId)).thenReturn(Optional.empty());

		assertAll(
				() -> mockMvc.perform(delete(linkWithParameter, targetId)).andExpect(status().isNotFound())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("timestamp",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()))))
						.andExpect(content().json("{'status': 404}"))
						.andExpect(jsonPath("errors[0]", is("Could not find target with id: " + targetId))),
				() -> verify(targetService, times(1)).delete(targetId),
				() -> verifyNoInteractions(targetModelAssembler));
	}

	public static String asJsonString(final Object obj) {

		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}
}
