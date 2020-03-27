package com.NowakArtur97.GlobalTerrorismAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.exception.TargetNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ApiResponses(value = { @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden") })
public class TargetController {

	private final TargetService targetService;

	private final TargetModelAssembler targetModelAssembler;

	private final PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

	@GetMapping(path = "/targets")
	@ApiOperation(value = "Find All Targets", notes = "Look up all targets", response = ResponseEntity.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)", defaultValue = "0"),
			@ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page", defaultValue = "100")
		})
	public ResponseEntity<PagedModel<TargetModel>> findAllTargets(
			@ApiIgnore("Ignored because swagger ui shows the wrong params, instead they are explained in the implicit params") 
			@PageableDefault(size = 100) Pageable pageable) {

		Page<TargetNode> targets = targetService.findAll(pageable);
		PagedModel<TargetModel> pagedModel = pagedResourcesAssembler.toModel(targets, targetModelAssembler);

		return new ResponseEntity<>(pagedModel, HttpStatus.OK);
	}

	@GetMapping(path = "/targets/{id}")
	@ApiOperation(value = "Find Target by id", notes = "Provide an id to look up specific target from all terrorism attacks targets", response = ResponseEntity.class)
	public ResponseEntity<TargetModel> findTargetById(
			@ApiParam(value = "ID value for the target you need to retrieve", required = true, example = "1") @PathVariable("id") Long id) {

		return targetService.findById(id).map(targetModelAssembler::toModel).map(ResponseEntity::ok)
				.orElseThrow(() -> new TargetNotFoundException(id));
	}
}
