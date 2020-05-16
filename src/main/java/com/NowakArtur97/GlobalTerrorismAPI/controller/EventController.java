package com.NowakArtur97.GlobalTerrorismAPI.controller;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.EventNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.EventTag;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = {EventTag.RESOURCE})
@ApiResponses(value = {@ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
public class EventController {

    private final EventService eventService;

    private final EventModelAssembler eventModelAssembler;

    private final PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

    private final PatchHelper patchHelper;

    private final ViolationHelper violationHelper;

    @GetMapping
    @ApiOperation(value = "Find All Events", notes = "Look up all events")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Displayed list of all Events", response = PagedModel.class)})
    @ApiPageable
    public ResponseEntity<PagedModel<EventModel>> findAllEvents(
            @ApiIgnore @PageableDefault(size = 100) Pageable pageable) {

        Page<EventNode> events = eventService.findAll(pageable);
        PagedModel<EventModel> pagedModel = pagedResourcesAssembler.toModel(events, eventModelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Find Event by id", notes = "Provide an id to look up specific Event from all terrorism attacks events")
    @ApiResponses({@ApiResponse(code = 200, message = "Event found by provided id", response = EventModel.class),
            @ApiResponse(code = 400, message = "Invalid Event id supplied"),
            @ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> findEventById(
            @ApiParam(value = "Event id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

        return eventService.findById(id).map(eventModelAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Add Event", notes = "Add new Event")
    @ApiResponses({@ApiResponse(code = 201, message = "Successfully added new Event", response = EventModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> addEvent(
            @ApiParam(value = "New Event", name = "event", required = true) @RequestBody @Valid EventDTO eventDTO) {

        EventNode eventNode = eventService.saveNew(eventDTO);

        EventModel eventModel = eventModelAssembler.toModel(eventNode);

        return new ResponseEntity<>(eventModel, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    @ApiOperation(value = "Update Event", notes = "Update Event. If the Event id is not found for update, a new Event with the next free id will be created")
    @ApiResponses({@ApiResponse(code = 201, message = "Successfully added new Event", response = EventModel.class),
            @ApiResponse(code = 200, message = "Successfully updated Event", response = EventModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> updateEvent(
            @ApiParam(value = "Id of the Event being updated", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "Event to update", name = "event", required = true) @RequestBody @Valid EventDTO eventDTO) {

        HttpStatus httpStatus;
        EventNode eventNode;

        Optional<EventNode> eventNodeOptional = eventService.findById(id);

        if (id != null && eventNodeOptional.isPresent()) {

            httpStatus = HttpStatus.OK;

            eventNode = eventService.update(eventNodeOptional.get(), eventDTO);

        } else {

            httpStatus = HttpStatus.CREATED;

            eventNode = eventService.saveNew(eventDTO);
        }

        EventModel eventModel = eventModelAssembler.toModel(eventNode);

        return new ResponseEntity<>(eventModel, httpStatus);
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @ApiOperation(value = "Update Event fields using Json Patch", notes = "Update Event fields using Json Patch")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Event fields", response = EventModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> updateEventFields(
            @ApiParam(value = "Id of the Event being updated", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "Event fields to update", name = "event", required = true) @RequestBody JsonPatch eventAsJsonPatch) {

        EventNode eventNode = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));

        EventNode eventNodePatched = patchHelper.patch(eventAsJsonPatch, eventNode, EventNode.class);

        violationHelper.violate(eventNodePatched, EventDTO.class);

        eventNodePatched = eventService.save(eventNodePatched);

        EventModel eventModel = eventModelAssembler.toModel(eventNodePatched);

        return new ResponseEntity<>(eventModel, HttpStatus.OK);
    }

    // id2 was used because Swagger does not allow two PATCH methods for the same
    // path – even if they have different parameters (parameters have no effect on
    // uniqueness)
    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @ApiOperation(value = "Update Event fields using Json Merge Patch", notes = "Update Event fields using Json Merge Patch")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Event fields", response = EventModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> updateEventFields(
            @ApiParam(value = "Id of the Event being updated", name = "id2", type = "integer", required = true, example = "1") @PathVariable("id2") Long id,
            @ApiParam(value = "Event fields to update", name = "event", required = true) @RequestBody JsonMergePatch eventAsJsonMergePatch) {

        EventNode eventNode = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));

        EventNode eventNodePatched = patchHelper.mergePatch(eventAsJsonMergePatch, eventNode, EventNode.class);

        violationHelper.violate(eventNodePatched, EventDTO.class);

        eventNodePatched = eventService.save(eventNodePatched);

        EventModel eventModel = eventModelAssembler.toModel(eventNodePatched);

        return new ResponseEntity<>(eventModel, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete Event by id", notes = "Provide an id to delete specific Event")
    @ApiResponses({@ApiResponse(code = 204, message = "Successfully deleted Event"),
            @ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class)})
    public ResponseEntity<Void> deleteEvent(
            @ApiParam(value = "Event id value needed to delete Event", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

        eventService.delete(id).orElseThrow(() -> new EventNotFoundException(id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
