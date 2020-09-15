package com.NowakArtur97.GlobalTerrorismAPI.feature.province;

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
@RequestMapping("/api/v1/provinces")
@Bulkable
@Api(tags = {ProvinceTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class ProvinceController extends GenericRestControllerImpl<ProvinceModel, ProvinceDTO, ProvinceNode> {

    ProvinceController(GenericService<ProvinceNode, ProvinceDTO> service,
                       RepresentationModelAssemblerSupport<ProvinceNode, ProvinceModel> modelAssembler,
                       PagedResourcesAssembler<ProvinceNode> pagedResourcesAssembler,
                       PatchUtil patchUtil, ViolationUtil<ProvinceNode, ProvinceDTO> violationUtil) {
        super(service, modelAssembler, pagedResourcesAssembler, patchUtil, violationUtil);
    }

    @GetMapping
    @Override
    @ApiOperation(value = "Find All Provinces", notes = "Look up all provinces")
    @ApiResponse(code = 200, message = "Displayed list of all Provinces", response = PagedModel.class)
    @ApiPageable
    public ResponseEntity<PagedModel<ProvinceModel>> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @ApiOperation(value = "Find Province by id", notes = "Provide an id to look up specific Province")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Province found by provided id", response = ProvinceModel.class),
            @ApiResponse(code = 400, message = "Invalid Province's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Province with provided id", response = ErrorResponse.class)})
    public ResponseEntity<ProvinceModel> findById(
            @ApiParam(value = "Province's id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.findById(id);
    }

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Add a Province", notes = "Add a new Province")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added a new Province", response = ProvinceModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<ProvinceModel> add(
            @ApiParam(value = "New Province", name = "province", required = true) @RequestBody @Valid ProvinceDTO dto) {
        return super.add(dto);
    }

    @PutMapping(path = "/{id}")
    @Override
    @ApiOperation(value = "Update a Province", notes = "Update a Province. If the Province's id is not found for update, a new Province with the next free id will be created")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated a Province", response = ProvinceModel.class),
            @ApiResponse(code = 201, message = "Successfully added new Province", response = ProvinceModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<ProvinceModel> update(
            @ApiParam(value = "Id of the Province being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Province to update", name = "province", required = true) @RequestBody @Valid ProvinceDTO dto) {
        return super.update(id, dto);
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update Province' fields using Json Patch", notes = "Update Province's fields using Json Patch",
            consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Province's fields", response = ProvinceModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<ProvinceModel> updateFields(
            @ApiParam(value = "Id of the Province being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Province's fields to update", name = "province", required = true)
            @RequestBody JsonPatch objectAsJsonPatch) {
        return super.updateFields(id, objectAsJsonPatch);
    }

    //     id2 was used because Swagger does not allow two PATCH methods for the same
//     path – even if they have different parameters (parameters have no effect on
//     uniqueness)
    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update Province's fields using Json Merge Patch", notes = "Update Province's fields using Json Merge Patch", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated Province's fields", response = ProvinceModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<ProvinceModel> updateFields(
            @ApiParam(value = "Id of the Province being updated", name = "id2", type = "integer", required = true, example = "1")
            @PathVariable("id2") Long id,
            @ApiParam(value = "Province's fields to update", name = "province", required = true)
            @RequestBody JsonMergePatch objectAsJsonMergePatch) {
        return super.updateFields(id, objectAsJsonMergePatch);
    }

    @DeleteMapping(path = "/{id}")
    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete Province by id", notes = "Provide an id to delete specific Province")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted Province"),
            @ApiResponse(code = 400, message = "Invalid Province's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Province with provided id", response = ErrorResponse.class)})
    public ResponseEntity<Void> delete(
            @ApiParam(value = "Province's id value needed to delete Province", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.delete(id);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Provinces resource options")
    @ApiResponse(code = 200, message = "Successfully found all Provinces resource options", response = ResponseEntity.class)
    public ResponseEntity<?> collectionOptions() {
        return super.collectionOptions();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Province resource options")
    @ApiResponse(code = 200, message = "Successfully found all Province resource options", response = ResponseEntity.class)
    public ResponseEntity<?> singularOptions() {
        return super.singularOptions();
    }
}
