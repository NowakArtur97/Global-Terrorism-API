package com.NowakArtur97.GlobalTerrorismAPI.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
@ApiResponses(value = { @ApiResponse(code = 401, message = "No permission to view resource"),
		@ApiResponse(code = 403, message = "Access to the resource is prohibited") })
public class TargetController {

	private final TargetService targetService;

	private final TargetModelAssembler targetModelAssembler;

	private final PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

	@GetMapping
	@ApiOperation(value = "Find All Targets", notes = "Look up all targets")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Displayed list of all Targets", response = PagedModel.class) })
	@ApiPageable
	public ResponseEntity<PagedModel<TargetModel>> findAllTargets(
			@ApiIgnore @PageableDefault(size = 100) Pageable pageable) {

		Page<TargetNode> targets = targetService.findAll(pageable);
		PagedModel<TargetModel> pagedModel = pagedResourcesAssembler.toModel(targets, targetModelAssembler);

		return new ResponseEntity<>(pagedModel, HttpStatus.OK);
	}

	@GetMapping(path = "/{id}")
	@ApiOperation(value = "Find Target by id", notes = "Provide an id to look up specific Target from all terrorism attacks targets")
	@ApiResponses({ @ApiResponse(code = 200, message = "Target found by provided id", response = TargetModel.class),
			@ApiResponse(code = 400, message = "Invalid Target id supplied"),
			@ApiResponse(code = 404, message = "Could not find Target with provided id", response = ErrorResponse.class) })
	public ResponseEntity<TargetModel> findTargetById(
			@ApiParam(value = "Target id value needed to retrieve details", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

		return targetService.findById(id).map(targetModelAssembler::toModel).map(ResponseEntity::ok)
				.orElseThrow(() -> new TargetNotFoundException(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) // Added to remove the default 200 status added by Swagger
	@ApiOperation(value = "Add Target", notes = "Add new Target")
	@ApiResponses({ @ApiResponse(code = 201, message = "Successfully added new Target", response = TargetModel.class),
			@ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class) })
	public ResponseEntity<TargetModel> addTarget(
			@ApiParam(value = "New Target", name = "target", required = true) @RequestBody @Valid TargetDTO targetDTO) {

		TargetNode targetNode = targetService.save(targetDTO);

		return new ResponseEntity<>((Optional.of(targetNode)).map(targetModelAssembler::toModel)
				.orElseThrow(() -> new TargetNotFoundException(targetNode.getId())), HttpStatus.CREATED);
	}

	@DeleteMapping
	@ApiOperation(value = "Delete Target by id", notes = "Provide an id to delete specific Target")
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully deleted Target", response = TargetModel.class),
			@ApiResponse(code = 404, message = "Could not find Target with provided id", response = ErrorResponse.class) })
	public ResponseEntity<TargetModel> deleteTarget(
			@ApiParam(value = "Target id value needed to delete Target", name = "id", type = "integer", required = true, example = "1") @PathVariable("id") Long id) {

		TargetNode targetNode = targetService.delete(id);

		return new ResponseEntity<>((Optional.of(targetNode)).map(targetModelAssembler::toModel)
				.orElseThrow(() -> new TargetNotFoundException(targetNode.getId())), HttpStatus.CREATED);
	}
}
