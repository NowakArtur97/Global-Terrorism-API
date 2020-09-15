package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.swagger.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.controller.BasicGenericRestControllerImpl;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.BasicGenericService;
import com.github.wnameless.spring.bulkapi.Bulkable;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/countries")
@Bulkable
@Api(tags = {CountryTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class CountryController extends BasicGenericRestControllerImpl<CountryModel, CountryNode> {

    CountryController(BasicGenericService<CountryNode> service,
                      RepresentationModelAssemblerSupport<CountryNode, CountryModel> modelAssembler,
                      PagedResourcesAssembler<CountryNode> pagedResourcesAssembler) {
        super(service, modelAssembler, pagedResourcesAssembler);
    }

    @GetMapping
    @Override
    @ApiOperation(value = "Find All Countries", notes = "Look up all countries")
    @ApiResponse(code = 200, message = "Displayed list of all Countries", response = PagedModel.class)
    @ApiPageable
    public ResponseEntity<PagedModel<CountryModel>> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @ApiOperation(value = "Find Country by id", notes = "Provide an id to look up specific Country")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Country found by provided id", response = CountryModel.class),
            @ApiResponse(code = 400, message = "Invalid Country's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Country with provided id", response = ErrorResponse.class)})
    public ResponseEntity<CountryModel> findById(
            @ApiParam(value = "Country's id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {
        return super.findById(id);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Countries resource options")
    @ApiResponse(code = 200, message = "Successfully found all Countries resource options", response = ResponseEntity.class)
    public ResponseEntity<?> collectionOptions() {
        return super.collectionOptions();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    @Override
    @ApiOperation(value = "Find all Country resource options")
    @ApiResponse(code = 200, message = "Successfully found all Country resource options", response = ResponseEntity.class)
    public ResponseEntity<?> singularOptions() {
        return super.singularOptions();
    }
}
