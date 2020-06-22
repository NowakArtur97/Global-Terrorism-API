package com.NowakArtur97.GlobalTerrorismAPI.controller.groupEvents;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.group.GroupEventsController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.page.PageHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupEventsController_Tests")
class GroupEventsControllerPostMethodTest {

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/groups";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";

    private MockMvc mockMvc;

    private GroupEventsController groupEventsController;

    private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

    @Mock
    private GroupService groupService;

    @Mock
    private RepresentationModelAssemblerSupport<GroupNode, GroupModel> groupModelAssembler;

    @Mock
    private RepresentationModelAssemblerSupport<EventNode, EventModel> eventModelAssembler;

    @Mock
    private PagedResourcesAssembler<EventNode> eventsPagedResourcesAssembler;

    @Mock
    private PageHelper pageHelper;

    private GroupBuilder groupBuilder;
    private EventBuilder eventBuilder;
    private TargetBuilder targetBuilder;

    @BeforeEach
    private void setUp() {

        groupEventsController = new GroupEventsController(groupService, groupModelAssembler, eventModelAssembler, eventsPagedResourcesAssembler, pageHelper);

        restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(groupEventsController, restResponseGlobalEntityExceptionHandler)
                .setControllerAdvice(new GenericRestControllerAdvice()).build();

        groupBuilder = new GroupBuilder();
        eventBuilder = new EventBuilder();
        targetBuilder = new TargetBuilder();
    }

    @Test
    void when_add_valid_event_to_group_should_return_group_with_new_event_as_model() {

        Long groupId = 1L;
        Long eventId = 2L;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).build(ObjectType.NODE);
        EventModel eventModel = (EventModel) eventBuilder.withId(eventId).build(ObjectType.MODEL);

        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        Link eventLink = new Link(pathToEventLink);
        eventModel.add(eventLink);

        GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupModel groupModel = (GroupModel) groupBuilder.withEventsCaused(List.of(eventModel)).build(ObjectType.MODEL);

        String pathToSelfLink = GROUP_BASE_PATH + "/" + eventId.intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupModel.getId().intValue() + "/events";
        groupModel.add(new Link(pathToSelfLink), new Link(pathToEventsLink));

        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        when(groupService.addEventToGroup(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(EventDTO.class))).thenReturn(Optional.of(groupNode));
        when(groupModelAssembler.toModel(groupNode)).thenReturn(groupModel);

        assertAll(
                () -> mockMvc
                        .perform(post(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToSelfLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupModel.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupModel.getName())))
                        .andExpect(jsonPath("eventsCaused[0].id", is(groupModel.getEventsCaused().get(0).getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(groupModel.getEventsCaused().get(0).getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(groupModel.getEventsCaused().get(0).getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(groupModel.getEventsCaused().get(0).getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicide", is(groupModel.getEventsCaused().get(0).getIsSuicide())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(groupModel.getEventsCaused().get(0).getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                is(groupModel.getEventsCaused().get(0).getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("eventsCaused[0].links[0].href", is(groupModel.getEventsCaused().get(0).getLink("self").get().getHref()))),
                () -> verify(groupService, times(1)).addEventToGroup(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(EventDTO.class)),
                () -> verifyNoMoreInteractions(groupService),
                () -> verify(groupModelAssembler, times(1)).toModel(groupNode),
                () -> verifyNoMoreInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }

    @Test
    void when_add_event_to_group_with_null_fields_should_return_errors() {

        Long groupId = 1L;
        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withSummary(null).withMotive(null).withDate(null)
                .withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicide(null).withTarget(null)
                .build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{event.summary.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{event.motive.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{event.date.notNull}")))
                        .andExpect(jsonPath("errors", hasItem("{event.isPartOfMultipleIncidents.notNull}")))
                        .andExpect(jsonPath("errors", hasItem("{event.isSuccessful.notNull}")))
                        .andExpect(jsonPath("errors", hasItem("{event.isSuicide.notNull}")))
                        .andExpect(jsonPath("errors", hasItem("{target.target.notBlank}"))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }

    @ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_add_event_to_group_with_invalid_target_should_return_errors(String invalidTarget) {

        Long groupId = 1L;
        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTarget).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc.perform(post(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(eventDTO))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{target.target.notBlank}"))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }

    @ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_add_event_to_group_with_invalid_summary_should_return_errors(String invalidSummary) {

        Long groupId = 1L;
        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(invalidSummary).withTarget(targetDTO)
                .build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{event.summary.notBlank}"))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }

    @ParameterizedTest(name = "{index}: For Event motive: {0} should have violation")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_add_event_to_group_with_invalid_motive_should_return_errors(String invalidMotive) {

        Long groupId = 1L;
        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withMotive(invalidMotive).withTarget(targetDTO)
                .build(ObjectType.DTO);

        assertAll(
                () -> mockMvc.perform(post(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(eventDTO))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{event.motive.notBlank}"))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }

    @Test
    void when_add_event_to_group_with_date_in_the_future_should_return_errors() {

        Long groupId = 1L;
        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        Calendar calendar = Calendar.getInstance();
        calendar.set(2090, Calendar.FEBRUARY, 1);
        Date invalidDate = calendar.getTime();
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withDate(invalidDate).withTarget(targetDTO).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc.perform(post(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(eventDTO))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{event.date.past}"))),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }

    @Test
    void when_add_event_to_group_but_group_not_exists_should_return_error_response() {

        Long groupId = 1L;
        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        when(groupService.addEventToGroup(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(EventDTO.class))).thenThrow(new ResourceNotFoundException("GroupModel", groupId));

        assertAll(
                () -> mockMvc.perform(post(linkWithParameter, groupId).content(ObjectTestMapper.asJsonString(eventDTO))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find GroupModel with id: " + groupId))),
                () -> verify(groupService, times(1)).addEventToGroup(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(EventDTO.class)),
                () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }
}
