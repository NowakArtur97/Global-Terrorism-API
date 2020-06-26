package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestControllerImpl;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.GroupTag;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
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
@RequestMapping("/api/v1/groups")
@Bulkable
@Api(tags = {GroupTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
public class GroupController extends GenericRestControllerImpl<GroupModel, GroupDTO, GroupNode> {

    GroupController(GenericService<GroupNode, GroupDTO> service, RepresentationModelAssemblerSupport<GroupNode, GroupModel> modelAssembler, PagedResourcesAssembler<GroupNode> pagedResourcesAssembler, PatchHelper patchHelper, ViolationHelper<GroupNode, GroupDTO> violationHelper) {
        super(service, modelAssembler, pagedResourcesAssembler, patchHelper, violationHelper);
    }

    @GetMapping
    @Override
    @ApiOperation(value = "Find All Groups", notes = "Look up all groups")
    @ApiResponse(code = 200, message = "Displayed list of all Groups", response = PagedModel.class)
    @ApiPageable
    public ResponseEntity<PagedModel<GroupModel>> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @ApiOperation(value = "Find Group by id", notes = "Provide an id to look up specific Group")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Group found by provided id", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Invalid Group id supplied"),
            @ApiResponse(code = 404, message = "Could not find Group with provided id", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> findById(
            @ApiParam(value = "Group id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {
        return super.findById(id);
    }

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Add Group", notes = "Add new Group")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added new Group", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> add(
            @ApiParam(value = "New Group", name = "group", required = true) @RequestBody @Valid GroupDTO dto) {
        return super.add(dto);
    }

    @PutMapping(path = "/{id}")
    @Override
    @ApiOperation(value = "Update Group", notes = "Update Group. If the Group id is not found for update, a new Group with the next free id will be created")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Group", response = GroupModel.class),
            @ApiResponse(code = 201, message = "Successfully added new Group", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> update(
            @ApiParam(value = "Id of the Group being updated", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "Group to update", name = "group", required = true) @RequestBody @Valid GroupDTO dto) {
        return super.update(id, dto);
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update Group fields using Json Patch", notes = "Update Group fields using Json Patch", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Group fields", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> updateFields(
            @ApiParam(value = "Id of the Group being updated", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "Group fields to update", name = "group", required = true) @RequestBody JsonPatch objectAsJsonPatch) {
        return super.updateFields(id, objectAsJsonPatch);
    }

    //     id2 was used because Swagger does not allow two PATCH methods for the same
//     path – even if they have different parameters (parameters have no effect on
//     uniqueness)
    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update Group fields using Json Merge Patch", notes = "Update Group fields using Json Merge Patch", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Group fields", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> updateFields(
            @ApiParam(value = "Id of the Group being updated", name = "id2", type = "integer", required = true, example = "1") @PathVariable("id2") Long id,
            @ApiParam(value = "Group fields to update", name = "group", required = true) @RequestBody JsonMergePatch objectAsJsonMergePatch) {
        return super.updateFields(id, objectAsJsonMergePatch);
    }

    @DeleteMapping(path = "/{id}")
    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete Group by id", notes = "Provide an id to delete specific Group")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted Group"),
            @ApiResponse(code = 404, message = "Could not find Group with provided id", response = ErrorResponse.class)})
    public ResponseEntity<Void> delete(
            @ApiParam(value = "Group id value needed to delete Group", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {
        return super.delete(id);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Groups resource options")
    @ApiResponse(code = 200, message = "Successfully found all Groups resource options", response = ResponseEntity.class)
    public ResponseEntity<?> collectionOptions() {
        return super.collectionOptions();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Group resource options")
    @ApiResponse(code = 200, message = "Successfully found all Group resource options", response = ResponseEntity.class)
    public ResponseEntity<?> singularOptions() {
        return super.singularOptions();
    }
}
