package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.EventTargetTag;
import com.github.wnameless.spring.bulkapi.Bulkable;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@Bulkable
@Api(tags = {EventTargetTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
@RequiredArgsConstructor
public class EventTargetController {

    private final GenericService<EventNode, EventDTO> eventService;

    private final RepresentationModelAssemblerSupport<TargetNode, TargetModel> targetModelAssembler;

    @GetMapping("/{id}/targets")
    @ApiOperation(value = "Find Event's Target by id", notes = "Provide an id to look up specific Event's Target")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Event's Target found by provided id", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Invalid Event's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> findEventTarget(@ApiParam(value = "Event's id value needed to retrieve target", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id, Pageable pageable) {

        TargetNode targetNode = eventService.findById(id).orElseThrow(() -> new ResourceNotFoundException("EventModel", id))
                .getTarget();

        TargetModel targetModel = targetModelAssembler.toModel(targetNode);

        return new ResponseEntity<>(targetModel, HttpStatus.OK);
    }
}
