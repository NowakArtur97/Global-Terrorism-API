package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.util.Calendar;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
public class EventControllerPostMethodTest {

	private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
	private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private GenericRestController<EventModel, EventDTO> eventController;

	private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

	@Mock
	private GenericService<EventNode, EventDTO> eventService;

	@Mock
	private EventModelAssembler modelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Mock
	private ViolationHelper violationHelper;

	private static TargetBuilder targetBuilder;
	private static EventBuilder eventBuilder;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchHelper,
				violationHelper);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(eventController, restResponseGlobalEntityExceptionHandler).build();

		targetBuilder = new TargetBuilder();
		eventBuilder = new EventBuilder();
	}

	@Test
	void when_add_valid_event_should_return_new_event_as_model() {

		Long eventId = 1L;

		TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
		TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
		TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
		String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
		targetModel.add(new Link(pathToTargetLink));
		EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
		EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
		EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).build(ObjectType.MODEL);

		String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
		Link eventLink = new Link(pathToEventLink);
		eventModel.add(eventLink);

		when(eventService.saveNew(ArgumentMatchers.any(EventDTO.class))).thenReturn(eventNode);
		when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(eventModel);

		assertAll(
				() -> mockMvc
						.perform(post(EVENT_BASE_PATH).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue())))
						.andExpect(jsonPath("summary", is(eventModel.getSummary())))
						.andExpect(jsonPath("motive", is(eventModel.getMotive())))
						.andExpect(jsonPath("date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(eventModel.getDate().toInstant().atZone(ZoneId.systemDefault())
												.toLocalDate()))))
						.andExpect(jsonPath("isSuicide", is(eventModel.getIsSuicide())))
						.andExpect(jsonPath("isSuccessful", is(eventModel.getIsSuccessful())))
						.andExpect(jsonPath("isPartOfMultipleIncidents", is(eventModel.getIsPartOfMultipleIncidents())))
						.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
						.andExpect(jsonPath("target.id", is(targetModel.getId().intValue())))
						.andExpect(jsonPath("target.target", is(targetModel.getTarget()))),
				() -> verify(eventService, times(1)).saveNew(ArgumentMatchers.any(EventDTO.class)),
				() -> verifyNoMoreInteractions(eventService),
				() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
				() -> verifyNoMoreInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper), () -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@Test
	void when_add_event_with_null_fields_should_return_errors() {

		EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withSummary(null).withMotive(null).withDate(null)
				.withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicide(null).withTarget(null)
				.build(ObjectType.DTO);

		assertAll(
				() -> mockMvc
						.perform(post(EVENT_BASE_PATH).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors", hasItem("{event.summary.notBlank}")))
						.andExpect(jsonPath("errors", hasItem("{event.motive.notBlank}")))
						.andExpect(jsonPath("errors", hasItem("{event.date.notNull}")))
						.andExpect(jsonPath("errors", hasItem("{event.isPartOfMultipleIncidents.notNull}")))
						.andExpect(jsonPath("errors", hasItem("{event.isSuccessful.notNull}")))
						.andExpect(jsonPath("errors", hasItem("{event.isSuicide.notNull}")))
						.andExpect(jsonPath("errors", hasItem("{target.target.notBlank}"))),
				() -> verifyNoInteractions(eventService), () -> verifyNoInteractions(modelAssembler),
				() -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void when_add_event_with_invalid_target_should_return_errors(String invalidTarget) {

		TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTarget).build(ObjectType.DTO);
		EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

		assertAll(
				() -> mockMvc
						.perform(post(EVENT_BASE_PATH).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors[0]", is("{target.target.notBlank}"))),
				() -> verifyNoInteractions(eventService), () -> verifyNoInteractions(modelAssembler),
				() -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void when_add_event_with_invalid_summary_should_return_errors(String invalidSummary) throws ParseException {

		TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
		EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(invalidSummary).withTarget(targetDTO)
				.build(ObjectType.DTO);

		assertAll(
				() -> mockMvc
						.perform(post(EVENT_BASE_PATH).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors[0]", is("{event.summary.notBlank}"))),
				() -> verifyNoInteractions(eventService), () -> verifyNoInteractions(modelAssembler),
				() -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@ParameterizedTest(name = "{index}: For Event motive: {0} should have violation")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void when_add_event_with_invalid_motive_should_return_errors(String invalidMotive) throws ParseException {

		TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
		EventDTO eventDTO = (EventDTO) eventBuilder.withMotive(invalidMotive).withTarget(targetDTO)
				.build(ObjectType.DTO);

		assertAll(
				() -> mockMvc
						.perform(post(EVENT_BASE_PATH).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors[0]", is("{event.motive.notBlank}"))),
				() -> verifyNoInteractions(eventService), () -> verifyNoInteractions(modelAssembler),
				() -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@Test
	void when_add_event_with_date_in_the_future_should_return_errors() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(2090, 1, 1);
		Date invalidDate = calendar.getTime();
		TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
		EventDTO eventDTO = (EventDTO) eventBuilder.withDate(invalidDate).withTarget(targetDTO).build(ObjectType.DTO);

		assertAll(
				() -> mockMvc
						.perform(post(EVENT_BASE_PATH).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors[0]", is("{event.date.past}"))),
				() -> verifyNoInteractions(eventService), () -> verifyNoInteractions(modelAssembler),
				() -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	public static String asJsonString(final Object obj) {

		try {

			return new ObjectMapper().writeValueAsString(obj);

		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}
}
