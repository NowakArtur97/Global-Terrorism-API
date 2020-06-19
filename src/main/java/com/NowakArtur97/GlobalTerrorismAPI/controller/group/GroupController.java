package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestControllerImpl;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.GroupTag;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
@Api(tags = {GroupTag.RESOURCE})
@ApiResponses(value = {@ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
public class GroupController extends GenericRestControllerImpl<GroupModel, GroupDTO, GroupNode> {

    GroupController(GenericService<GroupNode, GroupDTO> service, RepresentationModelAssemblerSupport<GroupNode, GroupModel> modelAssembler, PagedResourcesAssembler<GroupNode> pagedResourcesAssembler, PatchHelper patchHelper, ViolationHelper<GroupNode, GroupDTO> violationHelper) {
        super(service, modelAssembler, pagedResourcesAssembler, patchHelper, violationHelper);
    }
}
