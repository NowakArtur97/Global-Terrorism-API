package com.NowakArtur97.GlobalTerrorismAPI.controller.region;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.swagger.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.controller.BasicGenericRestControllerImpl;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.BasicGenericService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.RegionTag;
import com.github.wnameless.spring.bulkapi.Bulkable;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/regions")
@Bulkable
@Api(tags = {RegionTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
public class RegionController extends BasicGenericRestControllerImpl<RegionModel, RegionNode> {

        RegionController(BasicGenericService<RegionNode> service,
                         RepresentationModelAssemblerSupport<RegionNode, RegionModel> modelAssembler,
                         PagedResourcesAssembler<RegionNode> pagedResourcesAssembler) {
            super(service, modelAssembler, pagedResourcesAssembler);
        }

        @GetMapping
        @Override
        @ApiOperation(value = "Find All Regions", notes = "Look up all regions")
        @ApiResponse(code = 200, message = "Displayed list of all Regions", response = PagedModel.class)
        @ApiPageable
        public ResponseEntity<PagedModel<RegionModel>> findAll(Pageable pageable) {
            return super.findAll(pageable);
        }

        @GetMapping("/{id}")
        @Override
        @ApiOperation(value = "Find Region by id", notes = "Provide an id to look up specific Region")
        @ApiResponses({
                @ApiResponse(code = 200, message = "Region found by provided id", response = RegionModel.class),
                @ApiResponse(code = 400, message = "Invalid Region's id supplied"),
                @ApiResponse(code = 404, message = "Could not find Region with provided id", response = ErrorResponse.class)})
        public ResponseEntity<RegionModel> findById(
                @ApiParam(value = "Region's id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {
            return super.findById(id);
        }

        @RequestMapping(method = RequestMethod.OPTIONS)
        @Override
        @ApiOperation(value = "Find all Regions resource options")
        @ApiResponse(code = 200, message = "Successfully found all Regions resource options", response = ResponseEntity.class)
        public ResponseEntity<?> collectionOptions() {
            return super.collectionOptions();
        }

        @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
        @Override
        @ApiOperation(value = "Find all Region resource options")
        @ApiResponse(code = 200, message = "Successfully found all Region resource options", response = ResponseEntity.class)
        public ResponseEntity<?> singularOptions() {
            return super.singularOptions();
        }
}
