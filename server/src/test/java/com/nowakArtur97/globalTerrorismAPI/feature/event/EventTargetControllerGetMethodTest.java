package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModel;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.EventBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventTargetController_Tests")
class EventTargetControllerGetMethodTest {

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @Mock
    private RepresentationModelAssemblerSupport<TargetNode, TargetModel> targetModelAssembler;

    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        EventTargetController eventTargetController = new EventTargetController(eventService, targetModelAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(eventTargetController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_find_existing_event_target_should_return_target() {

        Long eventId = 3L;

        Long countryId = 1L;
        Long targetId = 2L;

        CountryNode countryNode = (CountryNode) countryBuilder.withId(countryId).build(ObjectType.NODE);
        CountryModel countryModel = (CountryModel) countryBuilder.withId(countryId).build(ObjectType.MODEL);
        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).withCountry(countryNode)
                .build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode)
                .build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).withCountry(countryModel)
                .build(ObjectType.MODEL);

        String pathToLink = TARGET_BASE_PATH + "/" + targetId.intValue();
        Link link = new Link(pathToLink);
        targetModel.add(link);

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
        when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink)))
                        .andExpect(jsonPath("id", is(targetId.intValue())))
                        .andExpect(jsonPath("target", is(targetModel.getTarget())))
                        .andExpect(jsonPath("countryOfOrigin.id", is(countryId.intValue())))
                        .andExpect(jsonPath("countryOfOrigin.name", is(countryModel.getName())))
                        .andExpect(jsonPath("countryOfOrigin.links").isEmpty()),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(targetModelAssembler, times(1)).toModel(targetNode),
                () -> verifyNoMoreInteractions(targetModelAssembler));
    }

    @Test
    void when_find_event_target_but_event_not_exists_should_return_error_response() {

        Long eventId = 1L;

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.findById(eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + eventId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }

    @Test
    void when_find_event_target_but_event_exists_without_target_should_return_error_response() {

        Long eventId = 1L;

        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).build(ObjectType.NODE);

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find TargetModel")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }
}
