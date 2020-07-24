package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerPutMethodTest {

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";

    private MockMvc mockMvc;

    private GenericRestController<GroupModel, GroupDTO> groupController;

    private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

    @Mock
    private GenericService<GroupNode, GroupDTO> groupService;

    @Mock
    private RepresentationModelAssemblerSupport<GroupNode, GroupModel> modelAssembler;

    @Mock
    private PagedResourcesAssembler<GroupNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Mock
    private ViolationHelper<GroupNode, GroupDTO> violationHelper;

    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;
    private static GroupBuilder groupBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
    }

    @BeforeEach
    private void setUp() {

        groupController = new GroupController(groupService, modelAssembler, pagedResourcesAssembler,
                patchHelper, violationHelper);

        restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(groupController, restResponseGlobalEntityExceptionHandler).build();
    }

    @Test
    void when_update_valid_group_should_return_updated_group_as_model() {

        Long eventId = 1L;
        Long groupId = 1L;

        String updatedGroupName = "new group name";

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);

        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        EventModel eventModel = (EventModel) eventBuilder.build(ObjectType.MODEL);

        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        eventModel.add(new Link(pathToEventLink));
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventId.intValue() + "/targets";
        eventModel.add(new Link(pathToTargetEventLink, "target"));

        GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        GroupNode updatedGroupNode = (GroupNode) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupModel updatedGroupModel = (GroupModel) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventModel)).build(ObjectType.MODEL);

        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupId.intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + updatedGroupModel.getId().intValue() + "/events";
        updatedGroupModel.add(new Link(pathToGroupLink), new Link(pathToEventsLink));

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
        when(groupService.update(ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any(GroupDTO.class)))
                .thenReturn(updatedGroupNode);
        when(modelAssembler.toModel(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupModel);

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(updatedGroupModel.getId().intValue())))
                        .andExpect(jsonPath("name", is(updatedGroupModel.getName())))
                        .andExpect(jsonPath("eventsCaused[0].id", is(eventModel.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventModel.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventModel.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date", is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(eventModel.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventModel.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventModel.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                is(eventModel.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToTargetEventLink))),
                () -> verify(groupService, times(1)).findById(groupId),
                () -> verify(groupService, times(1)).update(ArgumentMatchers.any(GroupNode.class),
                        ArgumentMatchers.any(GroupDTO.class)),
                () -> verifyNoMoreInteractions(groupService),
                () -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(GroupNode.class)),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_update_valid_group_with_events_should_return_updated_group_as_model() throws ParseException {

        Long eventId = 1L;
        Long groupId = 1L;

        String updatedSummary = "summary updated";
        String updatedMotive = "motive updated";
        Date updatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/08/2010 02:00:00:000");
        boolean updatedIsPartOfMultipleIncidents = false;
        boolean updatedIsSuccessful = false;
        boolean updatedIsSuicidal = false;

        String updatedGroupName = "new group name";

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);

        EventNode eventNode = (EventNode) eventBuilder.build(ObjectType.NODE);

        EventNode updatedEventNode = (EventNode) eventBuilder.withDate(updatedDate).withSummary(updatedSummary)
                .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).withIsSuccessful(updatedIsSuccessful)
                .withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive)
                .build(ObjectType.NODE);
        EventModel updatedEventModel = (EventModel) eventBuilder.withDate(updatedDate).withSummary(updatedSummary)
                .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).withIsSuccessful(updatedIsSuccessful)
                .withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive)
                .build(ObjectType.MODEL);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        updatedEventModel.add(new Link(pathToEventLink));
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventId.intValue() + "/targets";
        updatedEventModel.add(new Link(pathToTargetEventLink, "target"));

        GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);
        GroupNode updatedGroupNode = (GroupNode) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);
        GroupModel updatedGroupModel = (GroupModel) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(updatedEventModel)).build(ObjectType.MODEL);

        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupId.intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + updatedGroupModel.getId().intValue() + "/events";
        updatedGroupModel.add(new Link(pathToGroupLink), new Link(pathToEventsLink));

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.findById(groupId)).thenReturn(Optional.of(groupNode));
        when(groupService.update(ArgumentMatchers.any(GroupNode.class), ArgumentMatchers.any(GroupDTO.class)))
                .thenReturn(updatedGroupNode);
        when(modelAssembler.toModel(ArgumentMatchers.any(GroupNode.class))).thenReturn(updatedGroupModel);

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(updatedGroupModel.getId().intValue())))
                        .andExpect(jsonPath("name", is(updatedGroupModel.getName())))
                        .andExpect(jsonPath("eventsCaused[0].id", is(updatedEventModel.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(updatedEventModel.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(updatedEventModel.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(updatedEventModel.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(updatedEventModel.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(updatedEventModel.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                is(updatedEventModel.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToTargetEventLink))),
                () -> verify(groupService, times(1)).findById(groupId),
                () -> verify(groupService, times(1)).update(ArgumentMatchers.any(GroupNode.class),
                        ArgumentMatchers.any(GroupDTO.class)),
                () -> verifyNoMoreInteractions(groupService),
                () -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(GroupNode.class)),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_update_valid_group_with_not_existing_id_should_return_new_group_as_model() {

        Long eventId = 1L;
        Long groupId = 1L;

        String updatedGroupName = "new group name";

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);

        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        EventModel eventModel = (EventModel) eventBuilder.build(ObjectType.MODEL);

        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        eventModel.add(new Link(pathToEventLink));
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventId.intValue() + "/targets";
        eventModel.add(new Link(pathToTargetEventLink, "target"));

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);
        GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupModel groupModel = (GroupModel) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventModel)).build(ObjectType.MODEL);

        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupId.intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupModel.getId().intValue() + "/events";
        groupModel.add(new Link(pathToGroupLink), new Link(pathToEventsLink));

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.findById(groupId)).thenReturn(Optional.empty());
        when(groupService.saveNew(ArgumentMatchers.any(GroupDTO.class))).thenReturn(groupNode);
        when(modelAssembler.toModel(ArgumentMatchers.any(GroupNode.class))).thenReturn(groupModel);

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupModel.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupModel.getName())))
                        .andExpect(jsonPath("eventsCaused[0].id", is(eventModel.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventModel.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventModel.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventModel.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventModel.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                is(eventModel.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToTargetEventLink))),
                () -> verify(groupService, times(1)).findById(groupId),
                () -> verify(groupService, times(1)).saveNew(ArgumentMatchers.any(GroupDTO.class)),
                () -> verifyNoMoreInteractions(groupService),
                () -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(GroupNode.class)),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_update_group_with_null_fields_should_return_errors() {

        Long groupId = 1L;

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(null).withEventsCaused(null).build(ObjectType.DTO);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{group.name.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{group.eventsCaused.notEmpty}")))
                        .andExpect(jsonPath("errors", hasSize(2))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_update_group_with_null_event_fields_should_return_errors() {

        Long groupId = 1L;

        EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withSummary(null).withMotive(null).withDate(null)
                .withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicidal(null).withTarget(null)
                .build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{event.summary.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{event.motive.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{event.date.notNull}")))
                        .andExpect(jsonPath("errors", hasItem("{event.isPartOfMultipleIncidents.notNull}")))
                        .andExpect(jsonPath("errors", hasItem("{event.isSuccessful.notNull}")))
                        .andExpect(jsonPath("errors", hasItem("{event.isSuicidal.notNull}")))
                        .andExpect(jsonPath("errors", hasItem("{target.target.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(7))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_update_group_with_empty_events_list_should_return_errors() {

        Long groupId = 1L;

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(new ArrayList<>()).build(ObjectType.DTO);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{group.eventsCaused.notEmpty}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @ParameterizedTest(name = "{index}: For Group name: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_with_invalid_name_should_return_errors(String invalidName) {

        Long groupId = 1L;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(invalidName).withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{group.name.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @ParameterizedTest(name = "{index}: For Group Target: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_target_should_return_errors(String invalidTarget) {

        Long groupId = 1L;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTarget).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{target.target.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @ParameterizedTest(name = "{index}: For Group event summary: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_summary_should_return_errors(String invalidSummary) {

        Long groupId = 1L;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(invalidSummary).withTarget(targetDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{event.summary.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @ParameterizedTest(name = "{index}: For Group event motive: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_motive_should_return_errors(String invalidMotive) {

        Long groupId = 1L;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withMotive(invalidMotive).withTarget(targetDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{event.motive.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_update_group_event_with_date_in_the_future_should_return_errors() {

        Long groupId = 1L;

        Calendar calendar = Calendar.getInstance();
        calendar.set(2090, Calendar.FEBRUARY, 1);
        Date invalidDate = calendar.getTime();
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withDate(invalidDate).withTarget(targetDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{event.date.past}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }
}
