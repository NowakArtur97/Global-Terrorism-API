package com.NowakArtur97.GlobalTerrorismAPI.feature.target;

import com.NowakArtur97.GlobalTerrorismAPI.common.annotation.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.common.controller.GenericRestControllerImpl;
import com.NowakArtur97.GlobalTerrorismAPI.common.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.common.baseModel.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.common.service.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.common.util.PatchUtil;
import com.NowakArtur97.GlobalTerrorismAPI.common.util.ViolationUtil;
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
@RequestMapping("/api/v1/targets")
@Bulkable
@Api(tags = {TargetTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
public class TargetController extends GenericRestControllerImpl<TargetModel, TargetDTO, TargetNode> {

    public TargetController(GenericService<TargetNode, TargetDTO> service,
                            RepresentationModelAssemblerSupport<TargetNode, TargetModel> modelAssembler,
                            PagedResourcesAssembler<TargetNode> pagedResourcesAssembler,
                            PatchUtil patchUtil, ViolationUtil<TargetNode, TargetDTO> violationUtil) {
        super(service, modelAssembler, pagedResourcesAssembler, patchUtil, violationUtil);
    }

    @GetMapping
    @Override
    @ApiOperation(value = "Find All Targets", notes = "Look up all targets")
    @ApiResponse(code = 200, message = "Displayed list of all Targets", response = PagedModel.class)
    @ApiPageable
    public ResponseEntity<PagedModel<TargetModel>> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @ApiOperation(value = "Find Target by id", notes = "Provide an id to look up specific Target")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Target found by provided id", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Invalid Target's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Target with provided id", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> findById(
            @ApiParam(value = "Target's id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.findById(id);
    }

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Add a Target", notes = "Add a new Target")
    @ApiResponses(
            {@ApiResponse(code = 201, message = "Successfully added new Target", response = TargetModel.class),
                    @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> add(
            @ApiParam(value = "New Target", name = "target", required = true) @RequestBody @Valid TargetDTO dto) {
        return super.add(dto);
    }

    @PutMapping(path = "/{id}")
    @Override
    @ApiOperation(value = "Update a Target", notes = "Update a Target. If the Target's id is not found for update, a new Target with the next free id will be created")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated a  Target", response = TargetModel.class),
            @ApiResponse(code = 201, message = "Successfully added a new Target", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> update(
            @ApiParam(value = "Id of the Target being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Target to update", name = "target", required = true) @RequestBody @Valid TargetDTO dto) {
        return super.update(id, dto);
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update Target's fields using Json Patch", notes = "Update Target's fields using Json Patch",
            consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Target's fields", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> updateFields(
            @ApiParam(value = "Id of the Target being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Target's fields to update", name = "target", required = true) @RequestBody JsonPatch objectAsJsonPatch) {
        return super.updateFields(id, objectAsJsonPatch);
    }

    //     id2 was used because Swagger does not allow two PATCH methods for the same
//     path – even if they have different parameters (parameters have no effect on
//     uniqueness)
    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update Target's fields using Json Merge Patch", notes = "Update Target's fields using Json Merge Patch",
            consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Target's fields", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> updateFields(
            @ApiParam(value = "Id of the Target being updated", name = "id2", type = "integer", required = true, example = "1")
            @PathVariable("id2") Long id,
            @ApiParam(value = "Target's fields to update", name = "target", required = true)
            @RequestBody JsonMergePatch objectAsJsonMergePatch) {
        return super.updateFields(id, objectAsJsonMergePatch);
    }

    @DeleteMapping(path = "/{id}")
    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete Target by id", notes = "Provide an id to delete specific Target")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted Target"),
            @ApiResponse(code = 400, message = "Invalid Target's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Target with provided id", response = ErrorResponse.class)})
    public ResponseEntity<Void> delete(
            @ApiParam(value = "Target id value needed to delete Target", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.delete(id);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Targets resource options")
    @ApiResponse(code = 200, message = "Successfully found all Targets resource options", response = ResponseEntity.class)
    public ResponseEntity<?> collectionOptions() {
        return super.collectionOptions();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Target resource options")
    @ApiResponse(code = 200, message = "Successfully found all Target resource options", response = ResponseEntity.class)
    public ResponseEntity<?> singularOptions() {
        return super.singularOptions();
    }
}
