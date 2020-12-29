package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.github.wnameless.spring.bulkapi.Bulkable;
import com.nowakArtur97.globalTerrorismAPI.common.annotation.ApiPageable;
import com.nowakArtur97.globalTerrorismAPI.common.baseModel.ErrorResponse;
import com.nowakArtur97.globalTerrorismAPI.common.controller.GenericRestControllerImpl;
import com.nowakArtur97.globalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.nowakArtur97.globalTerrorismAPI.common.mediaType.PatchMediaType;
import com.nowakArtur97.globalTerrorismAPI.common.util.PatchUtil;
import com.nowakArtur97.globalTerrorismAPI.common.util.ViolationUtil;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/events")
@Bulkable
@Api(tags = {EventTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class EventController extends GenericRestControllerImpl<EventModel, EventDTO, EventNode> {

    private final EventService service;

    EventController(EventService service,
                    RepresentationModelAssemblerSupport<EventNode, EventModel> modelAssembler,
                    PagedResourcesAssembler<EventNode> pagedResourcesAssembler,
                    PatchUtil patchUtil, ViolationUtil<EventNode, EventDTO> violationUtil) {
        super(service, modelAssembler, pagedResourcesAssembler, patchUtil, violationUtil);
        this.service = service;
    }

    @GetMapping
    @Override
    @ApiOperation(value = "Find All Events", notes = "Look up all events")
    @ApiResponse(code = 200, message = "Displayed list of all Events", response = PagedModel.class)
    @ApiPageable
    public ResponseEntity<PagedModel<EventModel>> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @GetMapping("/depth/{depth}")
    @ApiOperation(value = "Find All Events with depth", notes = "Look up all events with depth")
    @ApiResponse(code = 200, message = "Displayed list of all Events", response = PagedModel.class)
    @ApiPageable
    public ResponseEntity<PagedModel<EventModel>> findAllWithDepth(Pageable pageable, @ApiParam(
            value = "Depth is responsible for the number of nested objects", name = "depth", type = "integer",
            required = true, example = "1")
    @PathVariable Integer depth) {

        depth = setDepth(depth);

        Page<EventNode> resources = service.findAll(pageable, depth);
        PagedModel<EventModel> pagedModel = pagedResourcesAssembler.toModel(resources, modelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Override
    @ApiOperation(value = "Find Event by id", notes = "Provide an id to look up specific Event")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Event found by provided id", response = EventModel.class),
            @ApiResponse(code = 400, message = "Invalid Event's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> findById(
            @ApiParam(value = "Event's id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.findById(id);
    }

    @GetMapping("/{id}/depth/{depth}")
    @ApiOperation(value = "Find Event by id and depth", notes = "Provide an id and depth to look up specific Event")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Event found by provided id and depth", response = EventModel.class),
            @ApiResponse(code = 400, message = "Invalid Event's id or depth supplied"),
            @ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> findByIdWithDepth(
            @ApiParam(value = "Event's id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Depth is responsible for the number of nested objects", name = "depth", type = "integer",
                    required = true, example = "1")
            @PathVariable Integer depth) {

        depth = setDepth(depth);

        return service.findById(id, depth).map(modelAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(modelType, id));
    }

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Add an Event", notes = "Add a new Event")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added a new Event", response = EventModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> add(
            @ApiParam(value = "New Event", name = "event", required = true) @RequestBody @Valid EventDTO dto) {
        return super.add(dto);
    }

    @PutMapping(path = "/{id}")
    @Override
    @ApiOperation(value = "Update an Event", notes = "Update Event. If the Event's id is not found for update, a new Event with the next free id will be created")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Event", response = EventModel.class),
            @ApiResponse(code = 201, message = "Successfully added new Event", response = EventModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> update(
            @ApiParam(value = "Id of the Event being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Event to update", name = "event", required = true) @RequestBody @Valid EventDTO dto) {
        return super.update(id, dto);
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update an Event's fields using Json Patch", notes = "Update Event's fields using Json Patch",
            consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Event's fields", response = EventModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> updateFields(
            @ApiParam(value = "Id of the Event being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Event's fields to update", name = "event", required = true) @RequestBody JsonPatch objectAsJsonPatch) {
        return super.updateFields(id, objectAsJsonPatch);
    }

    //     id2 was used because Swagger does not allow two PATCH methods for the same
    //     path – even if they have different parameters (parameters have no effect on
    //     uniqueness)
    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update Event's fields using Json Merge Patch", notes = "Update Event's fields using Json Merge Patch",
            consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Event's fields", response = EventModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<EventModel> updateFields(
            @ApiParam(value = "Id of the Event being updated", name = "id2", type = "integer", required = true, example = "1")
            @PathVariable("id2") Long id,
            @ApiParam(value = "Event's fields to update", name = "event", required = true) @RequestBody JsonMergePatch objectAsJsonMergePatch) {
        return super.updateFields(id, objectAsJsonMergePatch);
    }

    @DeleteMapping(path = "/{id}")
    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete Event by id", notes = "Provide an id to delete specific Event")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted Event"),
            @ApiResponse(code = 400, message = "Invalid Event's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Event with provided id", response = ErrorResponse.class)})
    public ResponseEntity<Void> delete(
            @ApiParam(value = "Event id value needed to delete Event", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.delete(id);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Events resource options")
    @ApiResponse(code = 200, message = "Successfully found all Events resource options", response = ResponseEntity.class)
    public ResponseEntity<?> collectionOptions() {
        return super.collectionOptions();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Event resource options")
    @ApiResponse(code = 200, message = "Successfully found all Event resource options", response = ResponseEntity.class)
    public ResponseEntity<?> singularOptions() {
        return super.singularOptions();
    }

    private Integer setDepth(Integer depth) {

        if (depth == null || depth > DEFAULT_DEPTH_FOR_JSON_PATCH) {
            depth = DEFAULT_DEPTH_FOR_JSON_PATCH;
        } else if (depth < 0) {
            depth = 0;
        }

        return depth;
    }
}
