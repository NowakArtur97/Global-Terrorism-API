package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.github.wnameless.spring.bulkapi.Bulkable;
import com.nowakArtur97.globalTerrorismAPI.common.annotation.ApiPageable;
import com.nowakArtur97.globalTerrorismAPI.common.baseModel.ErrorResponse;
import com.nowakArtur97.globalTerrorismAPI.common.controller.BasicGenericRestControllerImpl;
import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericService;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/victims")
@Bulkable
@Api(tags = {VictimTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class VictimController extends BasicGenericRestControllerImpl<VictimModel, VictimNode> {

    VictimController(BasicGenericService<VictimNode> service,
                     RepresentationModelAssemblerSupport<VictimNode, VictimModel> modelAssembler,
                     PagedResourcesAssembler<VictimNode> pagedResourcesAssembler) {
        super(service, modelAssembler, pagedResourcesAssembler);
    }

    @GetMapping
    @Override
    @ApiOperation(value = "Find All Victims", notes = "Look up all victims")
    @ApiResponse(code = 200, message = "Displayed list of all Victims", response = PagedModel.class)
    @ApiPageable
    public ResponseEntity<PagedModel<VictimModel>> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @ApiOperation(value = "Find Victim by id", notes = "Provide an id to look up specific Victim")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Victim found by provided id", response = VictimModel.class),
            @ApiResponse(code = 400, message = "Invalid Victim's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Victim with provided id", response = ErrorResponse.class)})
    public ResponseEntity<VictimModel> findById(
            @ApiParam(value = "Victim's id value needed to retrieve details", name = "id", type = "integer", required = true,
                    example = "1") @PathVariable("id") Long id) {
        return super.findById(id);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Victims resource options")
    @ApiResponse(code = 200, message = "Successfully found all Victims resource options", response = ResponseEntity.class)
    public ResponseEntity<?> collectionOptions() {
        return super.collectionOptions();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Victim resource options")
    @ApiResponse(code = 200, message = "Successfully found all Victim resource options", response = ResponseEntity.class)
    public ResponseEntity<?> singularOptions() {
        return super.singularOptions();
    }
}
