package com.NowakArtur97.GlobalTerrorismAPI.controller;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.ApiPageable;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.configuration.SwaggerConfiguration;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.TargetNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/targets")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = { SwaggerConfiguration.TARGET_TAG })
@ApiResponses(value = { @ApiResponse(code = 200, message = "Success response"),
		@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource sought was not found") })
public class TargetController {

	private final TargetService targetService;

	private final TargetModelAssembler targetModelAssembler;

	private final PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

	@GetMapping
	@ApiOperation(value = "Find All Targets", notes = "Look up all targets", response = ResponseEntity.class)
	@ApiResponses({ @ApiResponse(code = 404, message = "Targets not found") })
	@ApiPageable
	public ResponseEntity<PagedModel<TargetModel>> findAllTargets(
			@ApiIgnore @PageableDefault(size = 100) Pageable pageable) {

		Page<TargetNode> targets = targetService.findAll(pageable);
		PagedModel<TargetModel> pagedModel = pagedResourcesAssembler.toModel(targets, targetModelAssembler);

		return new ResponseEntity<>(pagedModel, HttpStatus.OK);
	}

	@GetMapping(path = "/{id}")
	@ApiOperation(value = "Find Target by id", notes = "Provide an id to look up specific target from all terrorism attacks targets", response = ResponseEntity.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Target found by provided id", response = ResponseEntity.class),
			@ApiResponse(code = 400, message = "Invalid Target id supplied"),
			@ApiResponse(code = 404, message = "Could not find target with provided id", response = ErrorResponse.class) })
	public ResponseEntity<TargetModel> findTargetById(
			@ApiParam(value = "Id value for the target you need to retrieve", required = true, example = "1") @PathVariable("id") Long id) {

		return targetService.findById(id).map(targetModelAssembler::toModel).map(ResponseEntity::ok)
				.orElseThrow(() -> new TargetNotFoundException(id));
	}

	@PostMapping
	@ApiOperation(value = "Add Target", notes = "Add new Target", response = ResponseEntity.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "Successfully added new Target", response = ResponseEntity.class) })
	public ResponseEntity<TargetModel> addTarget(@RequestBody @Valid TargetDTO targetDTO) {

		TargetNode targetNode = targetService.save(targetDTO);

		Long id = targetNode.getId();

		return new ResponseEntity<>(targetService.findById(id).map(targetModelAssembler::toModel)
				.orElseThrow(() -> new TargetNotFoundException(id)), HttpStatus.CREATED);
	}
}
