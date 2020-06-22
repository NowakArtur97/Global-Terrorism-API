package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.GroupEventsTag;
import com.NowakArtur97.GlobalTerrorismAPI.util.page.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@RequestMapping("/api/groups")
@Api(tags = {GroupEventsTag.RESOURCE})
@ApiResponses(value = {@ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
@RequiredArgsConstructor
public class GroupEventsController {

    private final GroupService groupService;

    private final RepresentationModelAssemblerSupport<GroupNode, GroupModel> groupModelAssembler;

    private final RepresentationModelAssemblerSupport<EventNode, EventModel> eventModelAssembler;

    private final PagedResourcesAssembler<EventNode> eventsPagedResourcesAssembler;

    private final PageHelper pageHelper;

    @GetMapping(path = "/{id}/events")
    public ResponseEntity<PagedModel<EventModel>> findGroupEvents(@PathVariable("id") Long id, Pageable pageable) {

        List<EventNode> eventsCausedByGroup = groupService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupModel", id)).getEventsCaused();

        PageImpl<EventNode> pages = pageHelper.convertListToPage(pageable, eventsCausedByGroup);

        PagedModel<EventModel> pagedModel = eventsPagedResourcesAssembler.toModel(pages, eventModelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @PostMapping(path = "/{id}/events")
    public ResponseEntity<GroupModel> addGroupEvent(@PathVariable("id") Long id, @RequestBody @Valid EventDTO dto) {

        GroupNode groupNode = groupService.addEventToGroup(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("GroupModel", id));

        GroupModel groupModel = groupModelAssembler.toModel(groupNode);

        return new ResponseEntity<>(groupModel, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}/events")
    public ResponseEntity<Void> deleteAllGroupEvents(@PathVariable("id") Long id) {

        groupService.deleteAllGroupEvents(id).orElseThrow(() -> new ResourceNotFoundException("GroupModel", id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/{id}/events", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> getOptions() {

        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.OPTIONS)
                .build();
    }
}
