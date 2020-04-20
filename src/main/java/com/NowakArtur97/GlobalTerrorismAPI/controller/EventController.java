package com.NowakArtur97.GlobalTerrorismAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@Api(tags = { EventTag.RESOURCE })
@ApiResponses(value = { @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
		@ApiResponse(code = 403, message = "Access to the resource is prohibited") })
public class EventController {

	private final EventService eventService;

	private final EventModelAssembler eventModelAssembler;

	private final PagedResourcesAssembler<EventNode> pagedResourcesAssembler;
}
