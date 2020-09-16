package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.common.annotation.ApiPageable;
import com.nowakArtur97.globalTerrorismAPI.common.controller.GenericRestControllerImpl;
import com.nowakArtur97.globalTerrorismAPI.common.mediaType.PatchMediaType;
import com.nowakArtur97.globalTerrorismAPI.common.baseModel.ErrorResponse;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;
import com.nowakArtur97.globalTerrorismAPI.common.util.PatchUtil;
import com.nowakArtur97.globalTerrorismAPI.common.util.ViolationUtil;
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
class GroupController extends GenericRestControllerImpl<GroupModel, GroupDTO, GroupNode> {

    GroupController(GenericService<GroupNode, GroupDTO> service,
                    RepresentationModelAssemblerSupport<GroupNode, GroupModel> modelAssembler,
                    PagedResourcesAssembler<GroupNode> pagedResourcesAssembler,
                    PatchUtil patchUtil, ViolationUtil<GroupNode, GroupDTO> violationUtil) {
        super(service, modelAssembler, pagedResourcesAssembler, patchUtil, violationUtil);
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
            @ApiResponse(code = 400, message = "Invalid Group's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Group with provided id", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> findById(
            @ApiParam(value = "Group's id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.findById(id);
    }

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Add a Group", notes = "Add a new Group")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added a new Group", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> add(
            @ApiParam(value = "New Group", name = "group", required = true) @RequestBody @Valid GroupDTO dto) {
        return super.add(dto);
    }

    @PutMapping(path = "/{id}")
    @Override
    @ApiOperation(value = "Update a Group", notes = "Update a Group. If the Group's id is not found for update, a new Group with the next free id will be created")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated a Group", response = GroupModel.class),
            @ApiResponse(code = 201, message = "Successfully added new Group", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> update(
            @ApiParam(value = "Id of the Group being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Group to update", name = "group", required = true) @RequestBody @Valid GroupDTO dto) {
        return super.update(id, dto);
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update Group' fields using Json Patch", notes = "Update Group's fields using Json Patch", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Group's fields", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> updateFields(
            @ApiParam(value = "Id of the Group being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Group's fields to update", name = "group", required = true) @RequestBody JsonPatch objectAsJsonPatch) {
        return super.updateFields(id, objectAsJsonPatch);
    }

    //     id2 was used because Swagger does not allow two PATCH methods for the same
//     path – even if they have different parameters (parameters have no effect on
//     uniqueness)
    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update Group's fields using Json Merge Patch", notes = "Update Group's fields using Json Merge Patch", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Group's fields", response = GroupModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<GroupModel> updateFields(
            @ApiParam(value = "Id of the Group being updated", name = "id2", type = "integer", required = true, example = "1")
            @PathVariable("id2") Long id,
            @ApiParam(value = "Group's fields to update", name = "group", required = true) @RequestBody JsonMergePatch objectAsJsonMergePatch) {
        return super.updateFields(id, objectAsJsonMergePatch);
    }

    @DeleteMapping(path = "/{id}")
    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete Group by id", notes = "Provide an id to delete specific Group")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted Group"),
            @ApiResponse(code = 400, message = "Invalid Group's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Group with provided id", response = ErrorResponse.class)})
    public ResponseEntity<Void> delete(
            @ApiParam(value = "Group's id value needed to delete Group", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
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
