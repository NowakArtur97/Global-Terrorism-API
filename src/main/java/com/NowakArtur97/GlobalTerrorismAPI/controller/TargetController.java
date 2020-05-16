package com.NowakArtur97.GlobalTerrorismAPI.controller;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.TargetNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.TargetTag;
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
@RequestMapping("/api/targets")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = {TargetTag.RESOURCE})
@ApiResponses(value = {@ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
public class TargetController {

    private final TargetService targetService;

    private final TargetModelAssembler targetModelAssembler;

    private final PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

    private final PatchHelper patchHelper;

    private final ViolationHelper violationHelper;

    @GetMapping
    @ApiOperation(value = "Find All Targets", notes = "Look up all targets")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Displayed list of all Targets", response = PagedModel.class)})
    @ApiPageable
    public ResponseEntity<PagedModel<TargetModel>> findAllTargets(
            @ApiIgnore @PageableDefault(size = 100) Pageable pageable) {

        Page<TargetNode> targets = targetService.findAll(pageable);
        PagedModel<TargetModel> pagedModel = pagedResourcesAssembler.toModel(targets, targetModelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Find Target by id", notes = "Provide an id to look up specific Target from all terrorism attacks targets")
    @ApiResponses({@ApiResponse(code = 200, message = "Target found by provided id", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Invalid Target id supplied"),
            @ApiResponse(code = 404, message = "Could not find Target with provided id", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> findTargetById(
            @ApiParam(value = "Target id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

        return targetService.findById(id).map(targetModelAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new TargetNotFoundException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Add Target", notes = "Add new Target")
    @ApiResponses({@ApiResponse(code = 201, message = "Successfully added new Target", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> addTarget(
            @ApiParam(value = "New Target", name = "target", required = true) @RequestBody @Valid TargetDTO targetDTO) {

        TargetNode targetNode = targetService.saveNew(targetDTO);

        TargetModel targetModel = targetModelAssembler.toModel(targetNode);

        return new ResponseEntity<>(targetModel, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    @ApiOperation(value = "Update Target", notes = "Update Target. If the Target id is not found for update, a new Target with the next free id will be created")
    @ApiResponses({@ApiResponse(code = 201, message = "Successfully added new Target", response = TargetModel.class),
            @ApiResponse(code = 200, message = "Successfully updated Target", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> updateTarget(
            @ApiParam(value = "Id of the Target being updated", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "Target to update", name = "target", required = true) @RequestBody @Valid TargetDTO targetDTO) {

        HttpStatus httpStatus;
        TargetNode targetNode;

        if (id != null && targetService.findById(id).isPresent()) {

            httpStatus = HttpStatus.OK;

            targetNode = targetService.update(id, targetDTO);

        } else {

            httpStatus = HttpStatus.CREATED;

            targetNode = targetService.saveNew(targetDTO);
        }

        TargetModel targetModel = targetModelAssembler.toModel(targetNode);

        return new ResponseEntity<>(targetModel, httpStatus);
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @ApiOperation(value = "Update Target fields using Json Patch", notes = "Update Target fields using Json Patch")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Target fields", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> updateTargetFields(
            @ApiParam(value = "Id of the Target being updated", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "Target fields to update", name = "target", required = true) @RequestBody JsonPatch targetAsJsonPatch) {

        TargetNode targetNode = targetService.findById(id).orElseThrow(() -> new TargetNotFoundException(id));

        TargetNode targetNodePatched = patchHelper.patch(targetAsJsonPatch, targetNode, TargetNode.class);

        violationHelper.violate(targetNodePatched, TargetDTO.class);

        targetNodePatched = targetService.save(targetNodePatched);

        TargetModel targetModel = targetModelAssembler.toModel(targetNodePatched);

        return new ResponseEntity<>(targetModel, HttpStatus.OK);
    }

    // id2 was used because Swagger does not allow two PATCH methods for the same
    // path – even if they have different parameters (parameters have no effect on
    // uniqueness)
    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @ApiOperation(value = "Update Target fields using Json Merge Patch", notes = "Update Target fields using Json Merge Patch")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Target fields", response = TargetModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> updateTargetFields(
            @ApiParam(value = "Id of the Target being updated", name = "id2", type = "integer", required = true, example = "1") @PathVariable("id2") Long id,
            @ApiParam(value = "Target fields to update", name = "target", required = true) @RequestBody JsonMergePatch targetAsJsonMergePatch) {

        TargetNode targetNode = targetService.findById(id).orElseThrow(() -> new TargetNotFoundException(id));

        TargetNode targetNodePatched = patchHelper.mergePatch(targetAsJsonMergePatch, targetNode, TargetNode.class);

        violationHelper.violate(targetNodePatched, TargetDTO.class);

        targetNodePatched = targetService.save(targetNodePatched);

        TargetModel targetModel = targetModelAssembler.toModel(targetNodePatched);

        return new ResponseEntity<>(targetModel, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @ApiOperation(value = "Delete Target by id", notes = "Provide an id to delete specific Target")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully deleted Target", response = TargetModel.class),
            @ApiResponse(code = 404, message = "Could not find Target with provided id", response = ErrorResponse.class)})
    public ResponseEntity<TargetModel> deleteTarget(
            @ApiParam(value = "Target id value needed to delete Target", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

        Optional<TargetNode> targetNode = targetService.delete(id);

        return new ResponseEntity<>(
                targetNode.map(targetModelAssembler::toModel).orElseThrow(() -> new TargetNotFoundException(id)),
                HttpStatus.OK);
    }
}
