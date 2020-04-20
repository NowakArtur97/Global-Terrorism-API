package com.NowakArtur97.GlobalTerrorismAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.EventTag;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
}
