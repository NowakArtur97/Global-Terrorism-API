package com.NowakArtur97.GlobalTerrorismAPI.controller.target;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
class TargetControllerGetMethodTest {

    private static int counterForUtilMethods = 0;

    private final String BASE_PATH = "http://localhost:8080/api/targets";

    private MockMvc mockMvc;

    private GenericRestController<TargetModel, TargetDTO> targetController;

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

        mockMvc = MockMvcBuilders.standaloneSetup(targetController).setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Test
    void when_find_all_targets_with_default_parameters_in_link_and_targets_exist_should_return_all_targets() {

        TargetNode targetNode1 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel1 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode2 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel2 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode3 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel3 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode4 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel4 = (TargetModel) createTarget(ObjectType.MODEL);

        List<TargetNode> targetsListExpected = List.of(targetNode1, targetNode2, targetNode3, targetNode4);
        List<TargetModel> targetModelsListExpected = List.of(targetModel1, targetModel2, targetModel3, targetModel4);
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
                        .andExpect(jsonPath("content[0].id", is(targetModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target", is(targetModel1.getTarget())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(targetModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].id", is(targetModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target", is(targetModel2.getTarget())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(targetModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].id", is(targetModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].target", is(targetModel3.getTarget())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(targetModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].id", is(targetModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].target", is(targetModel4.getTarget())))
                        .andExpect(
                                jsonPath("content[3].links[0].href", is(targetModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(targetService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(targetService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(targetsExpected, targetModelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler), () -> verifyNoMoreInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_targets_with_changed_parameters_in_link_and_targets_exist_should_return_all_targets() {

        TargetNode targetNode1 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel1 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode2 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel2 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode3 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel3 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode4 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel4 = (TargetModel) createTarget(ObjectType.MODEL);

        List<TargetNode> targetsListExpected = List.of(targetNode1, targetNode2, targetNode3, targetNode4);
        List<TargetModel> targetModelsListExpected = List.of(targetModel1, targetModel2, targetModel3, targetModel4);

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
                        .andExpect(jsonPath("content[0].id", is(targetModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target", is(targetModel1.getTarget())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(targetModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].id", is(targetModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target", is(targetModel2.getTarget())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(targetModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].id", is(targetModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].target", is(targetModel3.getTarget())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(targetModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].id", is(targetModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].target", is(targetModel4.getTarget())))
                        .andExpect(
                                jsonPath("content[3].links[0].href", is(targetModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(targetService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(targetService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(targetsExpected, targetModelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(targetModelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_targets_but_targets_not_exist_should_return_empty_list() {

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
                () -> verify(targetService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(targetService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(targetsExpected, targetModelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(targetModelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_existing_target_should_return_target() {

        Long targetId = 1L;
        String targetName = "target";
        TargetNode targetNode = new TargetNode(targetId, targetName);
        TargetModel targetModel = new TargetModel(targetId, targetName);

        String pathToLink = BASE_PATH + "/" + targetId.intValue();
        Link link = new Link(pathToLink);
        targetModel.add(link);

        String linkWithParameter = BASE_PATH + "/" + "{id}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

        assertAll(() -> mockMvc.perform(get(linkWithParameter, targetId)).andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink))).andExpect(jsonPath("id", is(targetId.intValue())))
                        .andExpect(jsonPath("target", is(targetName))),
                () -> verify(targetService, times(1)).findById(targetId), () -> verifyNoMoreInteractions(targetService),
                () -> verify(targetModelAssembler, times(1)).toModel(targetNode),
                () -> verifyNoMoreInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_target_but_target_not_exists_should_return_error_response() {

        Long targetId = 1L;

        String linkWithParameter = BASE_PATH + "/" + "{id}";

        when(targetService.findById(targetId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, targetId)).andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find TargetModel with id: " + targetId))),
                () -> verify(targetService, times(1)).findById(targetId), () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(targetModelAssembler), () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper));
    }

    private Target createTarget(ObjectType type) {

        switch (type) {

            case NODE:

                TargetNode targetNode = new TargetNode((long) counterForUtilMethods, "target" + counterForUtilMethods);

                return targetNode;

            case MODEL:

                TargetModel targetModel = new TargetModel((long) counterForUtilMethods, "target" + counterForUtilMethods);
                String pathToTargetLink = BASE_PATH + counterForUtilMethods;
                targetModel.add(new Link(pathToTargetLink));

                return targetModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}