package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PagedResourcesAssembler_Tests")
class GroupPagedResourcesAssemblerTest {

    private final String BASE_URL = "http://localhost";

    private PagedResourcesAssembler<GroupNode> pagedResourcesAssembler;

    private HateoasPageableHandlerMethodArgumentResolver resolver;

    private GroupModelAssembler groupModelAssembler;

    @Mock
    private EventModelAssembler eventModelAssembler;

    @Mock
    private ObjectMapper objectMapper;

    private TargetBuilder targetBuilder;
    private EventBuilder eventBuilder;
    private GroupBuilder groupBuilder;

    @BeforeAll
    private static void init() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @BeforeEach
    private void setUp() {

        resolver = new HateoasPageableHandlerMethodArgumentResolver();

        groupModelAssembler = new GroupModelAssembler(eventModelAssembler, objectMapper);

        pagedResourcesAssembler = new PagedResourcesAssembler<>(resolver, null);

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
    }

    @Test
    void when_map_group_node_page_to_paged_model_without_groups_should_return_empty_page() {

        int page = 0;
        int size = 1;
        int listSize = 0;

        List<GroupNode> groupsListExpected = groupsListExpected(listSize);

        Page<GroupNode> groupsPage = createPageOfGroupNodes(groupsListExpected, page, size);

        PagedModel<GroupModel> groupsPagedModel = pagedResourcesAssembler.toModel(groupsPage, groupModelAssembler);

        String selfLink = getLink(page, size);

        assertAll(
                () -> assertTrue(groupsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + groupsPagedModel),
                () -> assertTrue(groupsPagedModel.getLink("first").isEmpty(),
                        () -> "should not have first link, but had: "
                                + groupsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(groupsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + groupsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(groupsPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + groupsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(groupsPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + groupsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(groupsPagedModel.getLink("last").isEmpty(),
                        () -> "should not have last link, but had: "
                                + groupsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_group_node_page_to_paged_model_on_first_page_should_return_paged_model_with_links() {

        int page = 0;
        int size = 1;
        int listSize = 3;

        List<GroupNode> groupsListExpected = groupsListExpected(listSize);

        Page<GroupNode> groupsPage = createPageOfGroupNodes(groupsListExpected, page, size);

        PagedModel<GroupModel> groupsPagedModel = pagedResourcesAssembler.toModel(groupsPage, groupModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(groupsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + groupsPagedModel),
                () -> assertTrue(groupsPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("first").get().getHref(), firstLink, () -> "should have first link with url: " + firstLink + ", but had: "
                        + groupsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(groupsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + groupsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(groupsPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getNextLink().get().getHref(), nextLink, () -> "should have next link with url:" + nextLink + ", but had: "
                        + groupsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(groupsPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + groupsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(groupsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("last").get().getHref(), lastLink, () -> "should have last link with url: " + lastLink + ", but had: "
                        + groupsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_group_node_page_to_paged_model_on_last_page_should_return_paged_model_with_links() {

        int page = 2;
        int size = 1;
        int listSize = 3;

        List<GroupNode> groupsListExpected = groupsListExpected(listSize);

        Page<GroupNode> groupsPage = createPageOfGroupNodes(groupsListExpected, page, size);

        PagedModel<GroupModel> groupsPagedModel = pagedResourcesAssembler.toModel(groupsPage, groupModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(groupsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + groupsPagedModel),
                () -> assertTrue(groupsPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("first").get().getHref(), firstLink, () -> "should have first link with url: " + firstLink + ", but had: "
                        + groupsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(groupsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + groupsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(groupsPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + groupsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(groupsPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getPreviousLink().get().getHref(), previousLink, () -> "should have previous link with url:" + previousLink + ", but had: "
                        + groupsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(groupsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("last").get().getHref(), lastLink, () -> "should have last link with url: " + lastLink + ", but had: "
                        + groupsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_group_node_page_to_paged_model_with_previous_and_next_link_should_return_paged_model_with_links() {

        int page = 1;
        int size = 1;
        int listSize = 3;

        List<GroupNode> groupsListExpected = groupsListExpected(listSize);

        Page<GroupNode> groupsPage = createPageOfGroupNodes(groupsListExpected, page, size);

        PagedModel<GroupModel> groupsPagedModel = pagedResourcesAssembler.toModel(groupsPage, groupModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(groupsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + groupsPagedModel),
                () -> assertTrue(groupsPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("first").get().getHref(), firstLink, () -> "should have first link with url: " + firstLink + ", but had: "
                        + groupsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(groupsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + groupsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(groupsPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getNextLink().get().getHref(), nextLink, () -> "should have next link with url:" + nextLink + ", but had: "
                        + groupsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(groupsPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getPreviousLink().get().getHref(), previousLink, () -> "should have previous link with url:" + previousLink + ", but had: "
                        + groupsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(groupsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + groupsPagedModel),
                () -> assertEquals(groupsPagedModel.getLink("last").get().getHref(), lastLink, () -> "should have last link with url: " + lastLink + ", but had: "
                        + groupsPagedModel.getLink("last").get().getHref()));
    }

    private String getLink(int page, int size) {

        return new StringBuilder(BASE_URL).append("?page=").append(page).append("&size=").append(size).toString();
    }

    private List<GroupNode> groupsListExpected(int listSize) {

        List<GroupNode> groupsListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            TargetNode targetNode = (TargetNode) targetBuilder.withId((long) count).build(ObjectType.NODE);
            TargetModel targetModel = (TargetModel) targetBuilder.withId((long) count).build(ObjectType.MODEL);

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
            EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).build(ObjectType.MODEL);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

            when(objectMapper.map(groupNode, GroupModel.class)).thenReturn((GroupModel) groupBuilder.withEventsCaused(List.of(eventModel)).build(ObjectType.MODEL));

            groupsListExpected.add(groupNode);

            count++;
        }

        return groupsListExpected;
    }

    private Page<GroupNode> createPageOfGroupNodes(List<GroupNode> groupsListExpected, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        return new PageImpl<>(groupsListExpected, pageRequest, groupsListExpected.size());
    }
}
