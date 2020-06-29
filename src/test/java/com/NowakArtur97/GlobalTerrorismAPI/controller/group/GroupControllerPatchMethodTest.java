package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.GroupModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonMergePatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonPatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerPatchMethodTest {

    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";

    private MockMvc mockMvc;

    private GenericRestController<GroupModel, GroupDTO> groupController;

    private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

    @Mock
    private GenericService<GroupNode, GroupDTO> groupService;

    @Mock
    private GroupModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<GroupNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Autowired
    private ViolationHelper<GroupNode, GroupDTO> violationHelper;

    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;
    private static GroupBuilder groupBuilder;

    @BeforeEach
    private void setUp() {

        groupController = new GroupController(groupService, modelAssembler, pagedResourcesAssembler, patchHelper,
                violationHelper);

        restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(groupController, restResponseGlobalEntityExceptionHandler)
                .setMessageConverters(new JsonMergePatchHttpMessageConverter(), new JsonPatchHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter())
                .build();

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
    }

    @Nested
    class GroupControllerJsonPatchMethodTest {

        @Test
        void when_partial_update_valid_group_using_json_patch_should_return_partially_updated_node() {

            Long eventId = 1L;
            Long groupId = 1L;

            String updatedName = "updated group name";

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
            String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
            targetModel.add(new Link(pathToTargetLink));

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).build(ObjectType.MODEL);
            String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
            eventModel.add(new Link(pathToEventLink));

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withName(updatedName).withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

            GroupModel updatedGroupModel = (GroupModel) groupBuilder.withName(updatedName).withEventsCaused(List.of(eventModel)).build(ObjectType.MODEL);
            String pathToGroupLink = GROUP_BASE_PATH + "/" + groupId.intValue();
            String pathToEventsLink = GROUP_BASE_PATH + "/" + updatedGroupModel.getId().intValue() + "/events";
            updatedGroupModel.add(new Link(pathToGroupLink), new Link(pathToEventsLink));

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);
            when(groupService.save(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupNode);
            when(modelAssembler.toModel(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupModel);

            String jsonPatch = "["
                    + "{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedName + "\" }" + "]";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                            .andExpect(jsonPath("id", is(updatedGroupModel.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedGroupModel.getName())))
                            .andExpect(jsonPath("eventsCaused[0].id", is(updatedGroupModel.getEventsCaused().get(0).getId().intValue())))
                            .andExpect(jsonPath("eventsCaused[0].summary", is(updatedGroupModel.getEventsCaused().get(0).getSummary())))
                            .andExpect(jsonPath("eventsCaused[0].motive", is(updatedGroupModel.getEventsCaused().get(0).getMotive())))
                            .andExpect(jsonPath("eventsCaused[0].date", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(updatedGroupModel.getEventsCaused().get(0).getIsSuicidal())))
                            .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(updatedGroupModel.getEventsCaused().get(0).getIsSuccessful())))
                            .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                    is(updatedGroupModel.getEventsCaused().get(0).getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("eventsCaused[0].links[0].href", is(updatedGroupModel.getEventsCaused().get(0).getLink("self").get().getHref()))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verify(groupService, times(1)).save(ArgumentMatchers.any(GroupNode.class)),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(GroupNode.class)),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_valid_group_event_using_json_patch_should_return_partially_updated_node()
                throws ParseException {

            Long eventId = 1L;
            Long groupId = 1L;

            String updatedSummary = "summary updated";
            String updatedMotive = "motive updated";
            String updatedEventDateString = "2001-08-05";
            Date updatedEventDate = new SimpleDateFormat("yyyy-MM-dd").parse(updatedEventDateString);
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicidal = false;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
            String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
            targetModel.add(new Link(pathToTargetLink));

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            EventNode updatedEventNode = (EventNode) eventBuilder.withDate(updatedEventDate).withSummary(updatedSummary)
                    .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive)
                    .withTarget(targetNode).build(ObjectType.NODE);

            EventModel updatedEventModel = (EventModel) eventBuilder.withDate(updatedEventDate)
                    .withSummary(updatedSummary).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive)
                    .withTarget(targetModel).build(ObjectType.MODEL);
            String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
            updatedEventModel.add(new Link(pathToEventLink));

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            GroupModel updatedGroupModel = (GroupModel) groupBuilder.withEventsCaused(List.of(updatedEventModel)).build(ObjectType.MODEL);
            String pathToGroupLink = GROUP_BASE_PATH + "/" + groupId.intValue();
            String pathToEventsLink = GROUP_BASE_PATH + "/" + updatedGroupModel.getId().intValue() + "/events";
            updatedGroupModel.add(new Link(pathToGroupLink), new Link(pathToEventsLink));

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);
            when(groupService.save(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupNode);
            when(modelAssembler.toModel(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupModel);

            String jsonPatch = "["
                    + "{ \"op\": \"replace\", \"path\": \"/eventsCaused[0]/summary\", \"value\": \"" + updatedSummary
                    + "\" }," + "{ \"op\": \"replace\", \"path\": \"/eventsCaused[0]/motive\", \"value\": \"" + updatedMotive + "\" },"
                    + "{ \"op\": \"replace\", \"path\": \"/eventsCaused[0]/date\", \"value\": \"" + updatedEventDateString + "\" },"
                    + "{ \"op\": \"replace\", \"path\": \"/eventsCaused[0]/isPartOfMultipleIncidents\", \"value\": \""
                    + updatedIsPartOfMultipleIncidents + "\" },"
                    + "{ \"op\": \"replace\", \"path\": \"/eventsCaused[0]/isSuccessful\", \"value\": \"" + updatedIsSuccessful
                    + "\" }," + "{ \"op\": \"replace\", \"path\": \"/eventsCaused[0]/isSuicidal\", \"value\": \"" + updatedIsSuicidal + "\" }"
                    + "]";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                            .andExpect(jsonPath("id", is(updatedGroupModel.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedGroupModel.getName())))
                            .andExpect(jsonPath("eventsCaused[0].id", is(updatedGroupModel.getEventsCaused().get(0).getId().intValue())))
                            .andExpect(jsonPath("eventsCaused[0].summary", is(updatedGroupModel.getEventsCaused().get(0).getSummary())))
                            .andExpect(jsonPath("eventsCaused[0].motive", is(updatedGroupModel.getEventsCaused().get(0).getMotive())))
                            .andExpect(jsonPath("eventsCaused[0].date", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(updatedGroupModel.getEventsCaused().get(0).getIsSuicidal())))
                            .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(updatedGroupModel.getEventsCaused().get(0).getIsSuccessful())))
                            .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                    is(updatedGroupModel.getEventsCaused().get(0).getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("eventsCaused[0].links[0].href", is(updatedGroupModel.getEventsCaused().get(0).getLink("self").get().getHref()))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verify(groupService, times(1)).save(ArgumentMatchers.any(GroupNode.class)),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(GroupNode.class)),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_invalid_group_with_null_fields_using_json_patch_should_return_errors() {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withName(null).withEventsCaused(null).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonPatch = "[" + "{ \"op\": \"replace\", \"path\": \"/name\", \"value\": " + null + " },"
                    + "{ \"op\": \"replace\", \"path\": \"/eventsCaused\", \"value\": " + null + "}" + "]";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Group name cannot be empty")))
                            .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_invalid_group_with_empty_event_list_using_json_patch_should_return_errors() {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of()).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonPatch = "[" + "{ \"op\": \"replace\", \"path\": \"/eventsCaused\", \"value\": " + "\"[]\"" + "}" + "]";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @ParameterizedTest(name = "{index}: For Group name: {0} should have violation")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_group_with_invalid_name_using_json_patch_should_return_errors(
                String invalidName) {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withName(invalidName).withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + invalidName
                    + "\" }]";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Group name cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_invalid_group_event_with_null_fields_using_json_patch_should_return_errors() {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(null).build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withId(null).withSummary(null).withMotive(null)
                    .withDate(null).withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicidal(null)
                    .withTarget(updatedTargetNode).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonPatch = "[" + "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + null + "\" },"
                    + "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + null + "\" },"
                    + "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + null + "\" },"
                    + "{ \"op\": \"replace\", \"path\": \"/isPartOfMultipleIncidents\", \"value\": \"" + null + "\" },"
                    + "{ \"op\": \"replace\", \"path\": \"/isSuccessful\", \"value\": \"" + null + "\" },"
                    + "{ \"op\": \"replace\", \"path\": \"/isSuicidal\", \"value\": \"" + null + "\" },"
                    + "{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + null + "\" }" + "]";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue()))).andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", CoreMatchers.hasItem("Event summary cannot be empty")))
                            .andExpect(jsonPath("errors", CoreMatchers.hasItem("Event motive cannot be empty")))
                            .andExpect(jsonPath("errors", CoreMatchers.hasItem("Event date cannot be null")))
                            .andExpect(jsonPath("errors", CoreMatchers.hasItem(
                                    "Event must have information on whether it has been part of many incidents")))
                            .andExpect(jsonPath("errors",
                                    CoreMatchers.hasItem("Event must have information about whether it was successful")))
                            .andExpect(jsonPath("errors",
                                    CoreMatchers.hasItem("Event must have information about whether it was a suicidal attack"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @ParameterizedTest(name = "{index}: For Group event summary: {0} should have violation")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_group_event_with_invalid_summary_using_json_patch_should_return_errors(
                String invalidSummary) {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withSummary(invalidSummary)
                    .withTarget(updatedTargetNode).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused[0].summary\", \"value\": \"" + invalidSummary
                    + "\" }]";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @ParameterizedTest(name = "{index}: For Group event motive: {0} should have violation")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_group_event_with_invalid_motive_using_json_patch_should_return_errors(String invalidMotive) {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withMotive(invalidMotive)
                    .withTarget(updatedTargetNode).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused[0].motive\", \"value\": \"" + invalidMotive + "\" }]";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_group_event_with_date_in_the_future_using_json_patch_should_return_errors() {

            Long groupId = 1L;

            Calendar calendar = Calendar.getInstance();
            calendar.set(2090, Calendar.FEBRUARY, 1);
            Date invalidDate = calendar.getTime();

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withDate(invalidDate).withTarget(updatedTargetNode)
                    .build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused[0].date\", \"value\": \"" + invalidDate + "\" }]";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }
    }

    @Nested
    class GroupControllerMergeJsonPatchMethodTest {

        @Test
        void when_partial_update_valid_group_using_json_merge_patch_should_return_partially_updated_node() {

            Long eventId = 1L;
            Long groupId = 1L;

            String updatedName = "updated group name";

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
            String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
            targetModel.add(new Link(pathToTargetLink));

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).build(ObjectType.MODEL);
            String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
            eventModel.add(new Link(pathToEventLink));

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withName(updatedName).withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

            GroupModel updatedGroupModel = (GroupModel) groupBuilder.withName(updatedName).withEventsCaused(List.of(eventModel)).build(ObjectType.MODEL);
            String pathToGroupLink = GROUP_BASE_PATH + "/" + groupId.intValue();
            String pathToEventsLink = GROUP_BASE_PATH + "/" + updatedGroupModel.getId().intValue() + "/events";
            updatedGroupModel.add(new Link(pathToGroupLink), new Link(pathToEventsLink));

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);
            when(groupService.save(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupNode);
            when(modelAssembler.toModel(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupModel);

            String jsonMergePatch = "{ \"name\" : \"" + updatedName + "\" }";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                            .andExpect(jsonPath("id", is(updatedGroupModel.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedGroupModel.getName())))
                            .andExpect(jsonPath("eventsCaused[0].id", is(updatedGroupModel.getEventsCaused().get(0).getId().intValue())))
                            .andExpect(jsonPath("eventsCaused[0].summary", is(updatedGroupModel.getEventsCaused().get(0).getSummary())))
                            .andExpect(jsonPath("eventsCaused[0].motive", is(updatedGroupModel.getEventsCaused().get(0).getMotive())))
                            .andExpect(jsonPath("eventsCaused[0].date", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(updatedGroupModel.getEventsCaused().get(0).getIsSuicidal())))
                            .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(updatedGroupModel.getEventsCaused().get(0).getIsSuccessful())))
                            .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                    is(updatedGroupModel.getEventsCaused().get(0).getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("eventsCaused[0].links[0].href", is(updatedGroupModel.getEventsCaused().get(0).getLink("self").get().getHref()))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verify(groupService, times(1)).save(ArgumentMatchers.any(GroupNode.class)),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(GroupNode.class)),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_valid_group_with_events_using_json_merge_patch_should_return_partially_updated_node()
                throws ParseException {

            Long eventId = 1L;
            Long groupId = 1L;

            String updatedSummary = "summary updated";
            String updatedMotive = "motive updated";
            String updatedEventDateString = "2001-08-05";
            Date updatedEventDate = new SimpleDateFormat("yyyy-MM-dd").parse(updatedEventDateString);
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicidal = false;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
            String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
            targetModel.add(new Link(pathToTargetLink));

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            EventNode updatedEventNode = (EventNode) eventBuilder.withDate(updatedEventDate).withSummary(updatedSummary)
                    .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive)
                    .withTarget(targetNode).build(ObjectType.NODE);

            EventModel updatedEventModel = (EventModel) eventBuilder.withDate(updatedEventDate)
                    .withSummary(updatedSummary).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive)
                    .withTarget(targetModel).build(ObjectType.MODEL);
            String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
            updatedEventModel.add(new Link(pathToEventLink));

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            GroupModel updatedGroupModel = (GroupModel) groupBuilder.withEventsCaused(List.of(updatedEventModel)).build(ObjectType.MODEL);
            String pathToGroupLink = GROUP_BASE_PATH + "/" + groupId.intValue();
            String pathToEventsLink = GROUP_BASE_PATH + "/" + updatedGroupModel.getId().intValue() + "/events";
            updatedGroupModel.add(new Link(pathToGroupLink), new Link(pathToEventsLink));

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);
            when(groupService.save(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupNode);
            when(modelAssembler.toModel(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupModel);

            String jsonMergePatch = "{\"eventsCaused[0].summary\" : \"" + updatedSummary + "\", \"eventsCaused[0].motive\" : \"" + updatedMotive
                    + "\", \"eventsCaused[0].date\" : \"" + updatedEventDateString + "\", \"eventsCaused[0].isPartOfMultipleIncidents\" : "
                    + updatedIsPartOfMultipleIncidents + ", \"eventsCaused[0].isSuccessful\" : " + updatedIsSuccessful
                    + ", \"eventsCaused[0].isSuicidal\" : " + updatedIsSuicidal + "}";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                            .andExpect(jsonPath("id", is(updatedGroupModel.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedGroupModel.getName())))
                            .andExpect(jsonPath("eventsCaused[0].id", is(updatedGroupModel.getEventsCaused().get(0).getId().intValue())))
                            .andExpect(jsonPath("eventsCaused[0].summary", is(updatedGroupModel.getEventsCaused().get(0).getSummary())))
                            .andExpect(jsonPath("eventsCaused[0].motive", is(updatedGroupModel.getEventsCaused().get(0).getMotive())))
                            .andExpect(jsonPath("eventsCaused[0].date", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(updatedGroupModel.getEventsCaused().get(0).getIsSuicidal())))
                            .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(updatedGroupModel.getEventsCaused().get(0).getIsSuccessful())))
                            .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                    is(updatedGroupModel.getEventsCaused().get(0).getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("eventsCaused[0].links[0].href", is(updatedGroupModel.getEventsCaused().get(0).getLink("self").get().getHref()))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verify(groupService, times(1)).save(ArgumentMatchers.any(GroupNode.class)),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(GroupNode.class)),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_invalid_group_with_null_fields_using_json_merge_patch_should_return_errors() {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withName(null).withEventsCaused(null).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonMergePatch = "{\"name\" : \"" + null + "\", \"eventsCause\" : \"" + null + "\"}";
            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Group name cannot be empty")))
                            .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_invalid_group_with_empty_event_list_using_json_merge_patch_should_return_errors() {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of()).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonMergePatch = "{ \"eventsCaused\" : \"" + "[]" + "\" }";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @ParameterizedTest(name = "{index}: For Group name: {0} should have violation")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_group_with_invalid_name_using_json_merge_patch_should_return_errors(
                String invalidName) {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withName(invalidName).withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonMergePatch = "{ \"eventsCaused\" : \"" + invalidName + "\" }";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Group name cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_invalid_group_event_with_null_fields_using_json_merge_patch_should_return_errors() {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(null).build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withId(null).withSummary(null).withMotive(null)
                    .withDate(null).withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicidal(null)
                    .withTarget(updatedTargetNode).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonMergePatch = "{\"eventsCaused[0].summary\" : \"" + null + "\", \"eventsCaused[0].motive\" : \"" + null + "\", \"eventsCaused[0].date\" : \""
                    + null + "\", \"eventsCaused[0].isPartOfMultipleIncidents\" : " + null + ", \"eventsCaused[0].isSuccessful\" : " + null
                    + ", \"eventsCaused[0].isSuicidal\" : " + null + "}";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event summary cannot be empty")))
                            .andExpect(jsonPath("errors", hasItem("Event motive cannot be empty")))
                            .andExpect(jsonPath("errors", hasItem("Event date cannot be null")))
                            .andExpect(jsonPath("errors", hasItem(
                                    "Event must have information on whether it has been part of many incidents")))
                            .andExpect(jsonPath("errors",
                                    hasItem("Event must have information about whether it was successful")))
                            .andExpect(jsonPath("errors",
                                    hasItem("Event must have information about whether it was a suicidal attack")))
                            .andExpect(jsonPath("errors", hasItem("Target name cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @ParameterizedTest(name = "{index}: For Group event summary: {0} should have violation")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_group_event_with_invalid_summary_using_json_merge_patch_should_return_errors(
                String invalidSummary) {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withSummary(invalidSummary)
                    .withTarget(updatedTargetNode).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonMergePatch = "{ \"eventsCaused[0].summary\" : \"" + invalidSummary + "\" }";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @ParameterizedTest(name = "{index}: For Group event motive: {0} should have violation")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_group_event_with_invalid_motive_using_json_merge_patch_should_return_errors(
                String invalidMotive) {

            Long groupId = 1L;

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withMotive(invalidMotive)
                    .withTarget(updatedTargetNode).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonMergePatch = "{ \"eventsCaused[0].motive\" : \"" + invalidMotive + "\" }";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }

        @Test
        void when_partial_update_group_event_with_date_in_the_future_using_json_merge_patch_should_return_errors() {

            Long groupId = 1L;

            Calendar calendar = Calendar.getInstance();
            calendar.set(2090, 1, 1);
            Date invalidDate = calendar.getTime();

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withDate(invalidDate).withTarget(updatedTargetNode)
                    .build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode updatedGroupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

            when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
            when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(GroupNode.class),
                    ArgumentMatchers.any())).thenReturn(updatedGroupNode);

            String jsonMergePatch = "{ \"eventsCaused[0].date\" : \"" + invalidDate + "\" }";

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, groupId).content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future"))),
                    () -> verify(groupService, times(1)).findById(groupId),
                    () -> verifyNoMoreInteractions(groupService),
                    () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
                            ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any()),
                    () -> verifyNoMoreInteractions(patchHelper),
                    () -> verifyNoMoreInteractions(modelAssembler),
                    () -> verifyNoInteractions(pagedResourcesAssembler));
        }
    }
}
