package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.GroupEventsTag;
import com.NowakArtur97.GlobalTerrorismAPI.util.page.PageHelper;
import com.github.wnameless.spring.bulkapi.Bulkable;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@Bulkable
@Api(tags = {GroupEventsTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
@RequiredArgsConstructor
public class GroupEventsController {

    private final GroupService groupService;

    private final RepresentationModelAssemblerSupport<GroupNode, GroupModel> groupModelAssembler;

    private final RepresentationModelAssemblerSupport<EventNode, EventModel> eventModelAssembler;

    private final PagedResourcesAssembler<EventNode> eventsPagedResourcesAssembler;

    private final PageHelper pageHelper;

    @GetMapping("/{id}/events")
    @ApiOperation(value = "Find Group's Events by id", notes = "Provide an id to look up specific Group's Events")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Group's Events found by provided id", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Invalid Group's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Group with provided id", response = ErrorResponse.class)})
    @ApiPageable
    public ResponseEntity<PagedModel<EventModel>> findGroupEvents(@ApiParam(value = "Group's id value needed to retrieve events", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id, Pageable pageable) {

        List<EventNode> eventsCausedByGroup = groupService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupModel", id)).getEventsCaused();

        PageImpl<EventNode> pages = pageHelper.convertListToPage(pageable, eventsCausedByGroup);

        PagedModel<EventModel> pagedModel = eventsPagedResourcesAssembler.toModel(pages, eventModelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @PostMapping("/{id}/events")
    @ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Add Group's Event", notes = "Add a new Group's Event")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added a new Group's Event", response = EventModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Could not find Group with provided id", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> addGroupEvent(
            @ApiParam(value = "Group's id value needed to retrieve events", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "New Group's Event", name = "event", required = true) @RequestBody @Valid EventDTO dto) {

        GroupNode groupNode = groupService.addEventToGroup(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("GroupModel", id));

        GroupModel groupModel = groupModelAssembler.toModel(groupNode);

        return new ResponseEntity<>(groupModel, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/events")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete Group's Event by id", notes = "Provide an id to delete specific Group's Event")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted Group's Event"),
            @ApiResponse(code = 400, message = "Invalid Group's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Group with provided id", response = ErrorResponse.class)})
    public ResponseEntity<Void> deleteAllGroupEvents(@ApiParam(value = "Group's id value needed to delete events", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

        groupService.deleteAllGroupEvents(id).orElseThrow(() -> new ResourceNotFoundException("GroupModel", id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/{id}/events", method = RequestMethod.OPTIONS)
    @ApiOperation(value = "Find all Group's Events resource options")
    @ApiResponse(code = 200, message = "Successfully found all Group's Events resource options", response = ResponseEntity.class)
    public ResponseEntity<?> getOptions() {

        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.OPTIONS)
                .build();
    }
}
