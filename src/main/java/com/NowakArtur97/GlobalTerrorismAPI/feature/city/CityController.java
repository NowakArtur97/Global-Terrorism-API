package com.NowakArtur97.GlobalTerrorismAPI.feature.city;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.swagger.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestControllerImpl;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
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
@RequestMapping("/api/v1/cities")
@Bulkable
@Api(tags = {CityTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class CityController extends GenericRestControllerImpl<CityModel, CityDTO, CityNode> {

    CityController(GenericService<CityNode, CityDTO> service,
                          RepresentationModelAssemblerSupport<CityNode, CityModel> modelAssembler,
                          PagedResourcesAssembler<CityNode> pagedResourcesAssembler,
                          PatchHelper patchHelper, ViolationHelper<CityNode, CityDTO> violationHelper) {
        super(service, modelAssembler, pagedResourcesAssembler, patchHelper, violationHelper);
    }

    @GetMapping
    @Override
    @ApiOperation(value = "Find All Cities", notes = "Look up all cities")
    @ApiResponse(code = 200, message = "Displayed list of all Cities", response = PagedModel.class)
    @ApiPageable
    public ResponseEntity<PagedModel<CityModel>> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @ApiOperation(value = "Find City by id", notes = "Provide an id to look up specific City")
    @ApiResponses({
            @ApiResponse(code = 200, message = "City found by provided id", response = CityModel.class),
            @ApiResponse(code = 400, message = "Invalid City's id supplied"),
            @ApiResponse(code = 404, message = "Could not find City with provided id", response = ErrorResponse.class)})
    public ResponseEntity<CityModel> findById(
            @ApiParam(value = "City's id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.findById(id);
    }

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Add a City", notes = "Add a new City")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added a new City", response = CityModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<CityModel> add(
            @ApiParam(value = "New City", name = "city", required = true) @RequestBody @Valid CityDTO dto) {
        return super.add(dto);
    }

    @PutMapping(path = "/{id}")
    @Override
    @ApiOperation(value = "Update a City", notes = "Update a City. If the City's id is not found for update, a new City with the next free id will be created")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated a City", response = CityModel.class),
            @ApiResponse(code = 201, message = "Successfully added new City", response = CityModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<CityModel> update(
            @ApiParam(value = "Id of the City being updated", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "City to update", name = "city", required = true) @RequestBody @Valid CityDTO dto) {
        return super.update(id, dto);
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update City' fields using Json Patch", notes = "Update City's fields using Json Patch",
            consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated City's fields", response = CityModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<CityModel> updateFields(
            @ApiParam(value = "Id of the City being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "City's fields to update", name = "city", required = true) @RequestBody JsonPatch objectAsJsonPatch) {
        return super.updateFields(id, objectAsJsonPatch);
    }

    //     id2 was used because Swagger does not allow two PATCH methods for the same
//     path – even if they have different parameters (parameters have no effect on
//     uniqueness)
    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @Override
    @ApiOperation(value = "Update City's fields using Json Merge Patch", notes = "Update City's fields using Json Merge Patch",
            consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated City's fields", response = CityModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<CityModel> updateFields(
            @ApiParam(value = "Id of the City being updated", name = "id2", type = "integer", required = true, example = "1")
            @PathVariable("id2") Long id,
            @ApiParam(value = "City's fields to update", name = "city", required = true)
            @RequestBody JsonMergePatch objectAsJsonMergePatch) {
        return super.updateFields(id, objectAsJsonMergePatch);
    }

    @DeleteMapping(path = "/{id}")
    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete City by id", notes = "Provide an id to delete specific City")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted City"),
            @ApiResponse(code = 400, message = "Invalid City's id supplied"),
            @ApiResponse(code = 404, message = "Could not find City with provided id", response = ErrorResponse.class)})
    public ResponseEntity<Void> delete(
            @ApiParam(value = "City's id value needed to delete City", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.delete(id);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Cities resource options")
    @ApiResponse(code = 200, message = "Successfully found all Cities resource options", response = ResponseEntity.class)
    public ResponseEntity<?> collectionOptions() {
        return super.collectionOptions();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all City resource options")
    @ApiResponse(code = 200, message = "Successfully found all City resource options", response = ResponseEntity.class)
    public ResponseEntity<?> singularOptions() {
        return super.singularOptions();
    }
}
