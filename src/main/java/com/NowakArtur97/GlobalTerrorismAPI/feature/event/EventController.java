package com.NowakArtur97.GlobalTerrorismAPI.feature.event;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.swagger.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestControllerImpl;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchUtil;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationUtil;
import com.github.wnameless.spring.bulkapi.Bulkable;
import io.swagger.annotations.*;
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

    EventController(GenericService<EventNode, EventDTO> service,
                    RepresentationModelAssemblerSupport<EventNode, EventModel> modelAssembler,
                    PagedResourcesAssembler<EventNode> pagedResourcesAssembler,
                    PatchUtil patchUtil, ViolationUtil<EventNode, EventDTO> violationUtil) {
        super(service, modelAssembler, pagedResourcesAssembler, patchUtil, violationUtil);
    }

    @GetMapping
    @Override
    @ApiOperation(value = "Find All Events", notes = "Look up all events")
    @ApiResponse(code = 200, message = "Displayed list of all Events", response = PagedModel.class)
    @ApiPageable
    public ResponseEntity<PagedModel<EventModel>> findAll(Pageable pageable) {
        return super.findAll(pageable);
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
}
