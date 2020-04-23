package com.NowakArtur97.GlobalTerrorismAPI.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.EventNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.exception.TargetNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.EventTag;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = { EventTag.RESOURCE })
@ApiResponses(value = { @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
		@ApiResponse(code = 403, message = "Access to the resource is prohibited") })
public class EventController {

	private final EventService eventService;

	private final EventModelAssembler eventModelAssembler;

	private final PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@GetMapping
	@ApiOperation(value = "Find All Events", notes = "Look up all events")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Displayed list of all Events", response = PagedModel.class) })
	@ApiPageable
	public ResponseEntity<PagedModel<EventModel>> findAllEvents(
			@ApiIgnore @PageableDefault(size = 100) Pageable pageable) {

		Page<EventNode> events = eventService.findAll(pageable);
		PagedModel<EventModel> pagedModel = pagedResourcesAssembler.toModel(events, eventModelAssembler);

		return new ResponseEntity<>(pagedModel, HttpStatus.OK);
	}

	@GetMapping(path = "/{id}")
	@ApiOperation(value = "Find Event by id", notes = "Provide an id to look up specific Event from all terrorism attacks events")
	@ApiResponses({ @ApiResponse(code = 200, message = "Event found by provided id", response = EventModel.class),
			@ApiResponse(code = 400, message = "Invalid Event id supplied"),
			@ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class) })
	public ResponseEntity<EventModel> findEventById(
			@ApiParam(value = "Event id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

		return eventService.findById(id).map(eventModelAssembler::toModel).map(ResponseEntity::ok)
				.orElseThrow(() -> new EventNotFoundException(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
	@ApiOperation(value = "Add Event", notes = "Add new Event")
	@ApiResponses({ @ApiResponse(code = 201, message = "Successfully added new Event", response = EventModel.class),
			@ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class) })
	public ResponseEntity<EventModel> addEvent(
			@ApiParam(value = "New Event", name = "event", required = true) @RequestBody @Valid EventDTO eventDTO) {

		EventNode eventNode = eventService.saveNew(eventDTO);

		return new ResponseEntity<>((Optional.of(eventNode)).map(eventModelAssembler::toModel)
				.orElseThrow(() -> new TargetNotFoundException(eventNode.getId())), HttpStatus.CREATED);
	}

	@DeleteMapping(path = "/{id}")
	@ApiOperation(value = "Delete Event by id", notes = "Provide an id to delete specific Event")
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully deleted Event", response = EventModel.class),
			@ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class) })
	public ResponseEntity<EventModel> deleteEvent(
			@ApiParam(value = "Event id value needed to delete Event", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

		Optional<EventNode> eventNode = eventService.delete(id);

		return new ResponseEntity<>(
				eventNode.map(eventModelAssembler::toModel).orElseThrow(() -> new EventNotFoundException(id)),
				HttpStatus.OK);
	}
}
