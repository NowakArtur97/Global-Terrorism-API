package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
public class EventControllerPutMethodTest {

	private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
	private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private EventController eventController;

	private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

	@Mock
	private EventService eventService;

	@Mock
	private EventModelAssembler modelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Mock
	private ViolationHelper violationHelper;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchHelper,
				violationHelper);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(eventController, restResponseGlobalEntityExceptionHandler).build();
	}

	@Test
	void when_update_valid_event_should_return_updated_event_as_model() throws ParseException {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		String updatedSummary = "summary updated";
		String updatedMotive = "motive updated";
		Date updatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/08/2010 02:00:00:000");
		boolean updatedIsPartOfMultipleIncidents = false;
		boolean updatedIsSuccessful = false;
		boolean updatedIsSuicide = false;

		Long targetId = 1L;
		String target = "target";
		TargetDTO targetDTO = new TargetDTO(target);
		TargetNode targetNode = new TargetNode(targetId, target);
		TargetModel targetModel = new TargetModel(targetId, target);

		String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
		Link targetLink = new Link(pathToTargetLink);
		targetModel.add(targetLink);

		EventDTO eventDTO = EventDTO.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetDTO).build();

		EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		EventNode eventNodeUpdated = EventNode.builder().id(eventId).date(updatedDate).summary(updatedSummary)
				.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
				.isSuicide(updatedIsSuicide).motive(updatedMotive).target(targetNode).build();

		EventModel modelUpdated = EventModel.builder().id(eventId).date(updatedDate).summary(updatedSummary)
				.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
				.isSuicide(updatedIsSuicide).motive(updatedMotive).target(targetModel).build();

		String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
		Link eventLink = new Link(pathToEventLink);
		modelUpdated.add(eventLink);

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
		when(eventService.update(ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any(EventDTO.class)))
				.thenReturn(eventNodeUpdated);
		when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(modelUpdated);

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, eventId).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue())))
						.andExpect(jsonPath("summary", is(updatedSummary)))
						.andExpect(jsonPath("motive", is(updatedMotive)))
						.andExpect(jsonPath("date", is(notNullValue())))
						.andExpect(jsonPath("isSuicide", is(updatedIsSuicide)))
						.andExpect(jsonPath("isSuccessful", is(updatedIsSuccessful)))
						.andExpect(jsonPath("isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
						.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
						.andExpect(jsonPath("target.id", is(targetId.intValue())))
						.andExpect(jsonPath("target.target", is(target))),
				() -> verify(eventService, times(1)).findById(eventId),
				() -> verify(eventService, times(1)).update(ArgumentMatchers.any(EventNode.class),
						ArgumentMatchers.any(EventDTO.class)),
				() -> verifyNoMoreInteractions(eventService),
				() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
				() -> verifyNoMoreInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper), () -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@Test
	void when_update_valid_event_with_updated_target_should_return_updated_event_as_model_with_updated_target()
			throws ParseException {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		String updatedSummary = "summary updated";
		String updatedMotive = "motive updated";
		Date updatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/08/2010 02:00:00:000");
		boolean updatedIsPartOfMultipleIncidents = false;
		boolean updatedIsSuccessful = false;
		boolean updatedIsSuicide = false;

		Long targetId = 1L;
		String target = "target";
		String updatedTarget = "target updated";
		TargetDTO targetDTO = new TargetDTO(target);
		TargetNode targetNode = new TargetNode(targetId, target);
		TargetNode targetNodeUpdated = new TargetNode(targetId, updatedTarget);
		TargetModel targetModel = new TargetModel(targetId, target);
		TargetModel targetModelUpdated = new TargetModel(targetId, updatedTarget);

		String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
		Link targetLink = new Link(pathToTargetLink);
		targetModel.add(targetLink);
		targetModelUpdated.add(targetLink);

		EventDTO eventDTO = EventDTO.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetDTO).build();

		EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		EventNode eventNodeUpdated = EventNode.builder().id(eventId).date(updatedDate).summary(updatedSummary)
				.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
				.isSuicide(updatedIsSuicide).motive(updatedMotive).target(targetNodeUpdated).build();

		EventModel modelUpdated = EventModel.builder().id(eventId).date(updatedDate).summary(updatedSummary)
				.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
				.isSuicide(updatedIsSuicide).motive(updatedMotive).target(targetModelUpdated).build();

		String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
		Link eventLink = new Link(pathToEventLink);
		modelUpdated.add(eventLink);

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
		when(eventService.update(ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any(EventDTO.class)))
				.thenReturn(eventNodeUpdated);
		when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(modelUpdated);

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, eventId).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue())))
						.andExpect(jsonPath("summary", is(updatedSummary)))
						.andExpect(jsonPath("motive", is(updatedMotive)))
						.andExpect(jsonPath("date", is(notNullValue())))
						.andExpect(jsonPath("isSuicide", is(updatedIsSuicide)))
						.andExpect(jsonPath("isSuccessful", is(updatedIsSuccessful)))
						.andExpect(jsonPath("isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
						.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
						.andExpect(jsonPath("target.id", is(targetId.intValue())))
						.andExpect(jsonPath("target.target", is(updatedTarget))),
				() -> verify(eventService, times(1)).findById(eventId),
				() -> verify(eventService, times(1)).update(ArgumentMatchers.any(EventNode.class),
						ArgumentMatchers.any(EventDTO.class)),
				() -> verifyNoMoreInteractions(eventService),
				() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
				() -> verifyNoMoreInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper), () -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@Test
	void when_update_valid_event_with_not_existing_id_should_return_new_event_as_model() throws ParseException {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		Long targetId = 1L;
		String target = "target";
		TargetDTO targetDTO = new TargetDTO(target);
		TargetNode targetNode = new TargetNode(targetId, target);
		TargetModel targetModel = new TargetModel(targetId, target);

		String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
		Link targetLink = new Link(pathToTargetLink);
		targetModel.add(targetLink);

		EventDTO eventDTO = EventDTO.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetDTO).build();

		EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		EventModel model = EventModel.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetModel).build();

		String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
		Link eventLink = new Link(pathToEventLink);
		model.add(eventLink);

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		when(eventService.findById(eventId)).thenReturn(Optional.empty());
		when(eventService.saveNew(ArgumentMatchers.any(EventDTO.class))).thenReturn(eventNode);
		when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(model);

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, eventId).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue()))).andExpect(jsonPath("summary", is(summary)))
						.andExpect(jsonPath("motive", is(motive))).andExpect(jsonPath("date", is(notNullValue())))
						.andExpect(jsonPath("isSuicide", is(isSuicide)))
						.andExpect(jsonPath("isSuccessful", is(isSuccessful)))
						.andExpect(jsonPath("isPartOfMultipleIncidents", is(isPartOfMultipleIncidents)))
						.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
						.andExpect(jsonPath("target.id", is(targetId.intValue())))
						.andExpect(jsonPath("target.target", is(target))),
				() -> verify(eventService, times(1)).findById(eventId),
				() -> verify(eventService, times(1)).saveNew(ArgumentMatchers.any(EventDTO.class)),
				() -> verifyNoMoreInteractions(eventService),
				() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
				() -> verifyNoMoreInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper), () -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@Test
	void when_update_event_with_null_fields_should_return_errors() {

		Long eventId = 1L;

		String summary = null;
		String motive = null;
		Date date = null;
		Boolean isPartOfMultipleIncidents = null;
		Boolean isSuccessful = null;
		Boolean isSuicide = null;

		String target = "target";
		TargetDTO targetDTO = new TargetDTO(target);

		EventDTO eventDTO = EventDTO.builder().date(date).summary(summary).motive(motive)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.target(targetDTO).build();

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, eventId).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors", hasItem("{event.summary.notBlank}")))
						.andExpect(jsonPath("errors", hasItem("{event.motive.notBlank}")))
						.andExpect(jsonPath("errors", hasItem("{event.date.notNull}")))
						.andExpect(jsonPath("errors", hasItem("{event.isPartOfMultipleIncidents.notNull}")))
						.andExpect(jsonPath("errors", hasItem("{event.isSuccessful.notNull}")))
						.andExpect(jsonPath("errors", hasItem("{event.isSuicide.notNull}"))),
				() -> verifyNoInteractions(eventService), () -> verifyNoInteractions(modelAssembler),
				() -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void when_update_event_with_invalid_target_should_return_errors(String target) throws ParseException {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		TargetDTO targetDTO = new TargetDTO(target);

		EventDTO eventDTO = EventDTO.builder().date(date).summary(summary).motive(motive)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.target(targetDTO).build();

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, eventId).content(asJsonString(eventDTO))
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
	void when_update_event_with_invalid_summary_should_return_errors(String invalidSummary) throws ParseException {

		Long eventId = 1L;

		String motive = "motive";
		Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		String target = "target";
		TargetDTO targetDTO = new TargetDTO(target);

		EventDTO eventDTO = EventDTO.builder().date(date).summary(invalidSummary).motive(motive)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.target(targetDTO).build();

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, eventId).content(asJsonString(eventDTO))
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
	void when_update_event_with_invalid_motive_should_return_errors(String invalidMotive) throws ParseException {

		Long eventId = 1L;

		String summary = "summary";
		Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		String target = "target";
		TargetDTO targetDTO = new TargetDTO(target);

		EventDTO eventDTO = EventDTO.builder().date(date).summary(summary).motive(invalidMotive)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.target(targetDTO).build();

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, eventId).content(asJsonString(eventDTO))
								.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
						.andExpect(jsonPath("status", is(400)))
						.andExpect(jsonPath("errors[0]", is("{event.motive.notBlank}"))),
				() -> verifyNoInteractions(eventService), () -> verifyNoInteractions(modelAssembler),
				() -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper),
				() -> verifyNoInteractions(pagedResourcesAssembler));
	}

	@Test
	void when_update_event_with_date_in_the_future_should_return_errors() {

		Long eventId = 1L;

		@SuppressWarnings("deprecation")
		Date invalidEventDate = new Date(2099, 12, 31);

		String motive = "motive";
		String summary = "summary";
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		String target = "target";
		TargetDTO targetDTO = new TargetDTO(target);

		EventDTO eventDTO = EventDTO.builder().date(invalidEventDate).summary(summary).motive(motive)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.target(targetDTO).build();

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		assertAll(
				() -> mockMvc
						.perform(put(linkWithParameter, eventId).content(asJsonString(eventDTO))
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
