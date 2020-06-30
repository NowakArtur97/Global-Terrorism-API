package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.EventTargetTag;
import com.github.wnameless.spring.bulkapi.Bulkable;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/events")
@Bulkable
@Api(tags = {EventTargetTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
@RequiredArgsConstructor
public class EventTargetController {

    private final EventService eventService;

    private final RepresentationModelAssemblerSupport<TargetNode, TargetModel> targetModelAssembler;

    @GetMapping("/{id}/targets")
    @ApiOperation(value = "Find Event's Target by id", notes = "Provide an id to look up specific Event's Target")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Event's Target found by provided id", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Invalid Event's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> findEventTarget(@ApiParam(value = "Event's id value needed to retrieve target", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

        TargetNode targetNode = eventService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventModel", id))
                .getTarget();

        if (targetNode == null) {
            throw new ResourceNotFoundException("TargetModel");
        }

        return new ResponseEntity<>(targetModelAssembler.toModel(targetNode), HttpStatus.OK);
    }

    @PutMapping("/{id}/targets")
    @ApiOperation(value = "Update an Event's Target", notes = "Update an Event's Target. If the Event's Target id is not found for update, a new Target with the next free id will be created")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated an Event's Target", response = TargetModel.class),
            @ApiResponse(code = 201, message = "Successfully added a new Event's Target", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> updateEventTarget(
            @ApiParam(value = "Id of the Target's Event being updated", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "Events's Target to update", name = "target", required = true) @RequestBody @Valid TargetDTO targetDTO) {

        EventNode eventNode = eventService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventModel", id));
        HttpStatus httpStatus;

        if (eventNode.getTarget() != null) {

            httpStatus = HttpStatus.OK;
        } else {

            httpStatus = HttpStatus.CREATED;
        }

        TargetNode targetNode = eventService.addOrUpdateEventTarget(eventNode, targetDTO).getTarget();

        return new ResponseEntity<>(targetModelAssembler.toModel(targetNode), httpStatus);
    }

    @DeleteMapping("/{id}/targets")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete Event's Target by id", notes = "Provide an id to delete specific Event's Target")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted Event's Target"),
            @ApiResponse(code = 400, message = "Invalid Event's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class)})
    public ResponseEntity<Void> deleteAllGroupEvents(@ApiParam(value = "Event's id value needed to delete target", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

        eventService.deleteEventTarget(id).orElseThrow(() -> new ResourceNotFoundException("EventModel", id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
