package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.GroupTag;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@Api(tags = {GroupTag.RESOURCE})
@ApiResponses(value = {@ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
@RequiredArgsConstructor
public class GroupEventsController {

    private final GroupService groupService;

    private final RepresentationModelAssemblerSupport<EventNode, EventModel> eventsModelAssembler;

    private final PagedResourcesAssembler<EventNode> eventsPagedResourcesAssembler;

    private final PageHelper pageHelper;

    @GetMapping(path = "/{id}/events")
    public ResponseEntity<PagedModel<?>> findGroupEvents(@PathVariable("id") Long id, Pageable pageable) {

        List<EventNode> eventsCausedByGroup = groupService.findAllEventsCausedByGroup(id);

        if (pageable.getOffset() >= eventsCausedByGroup.size()) {
            return new ResponseEntity<>(PagedModel.NO_PAGE, HttpStatus.OK);
        }

        PageImpl<EventNode> pages = pageHelper.convertListToPage(pageable, eventsCausedByGroup);

        PagedModel<EventModel> pagedModel = eventsPagedResourcesAssembler.toModel(pages, eventsModelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }
}
