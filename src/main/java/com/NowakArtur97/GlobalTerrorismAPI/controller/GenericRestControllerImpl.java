package com.NowakArtur97.GlobalTerrorismAPI.controller;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchUtil;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationUtil;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;
import java.util.Optional;

@RestController
public abstract class GenericRestControllerImpl<M extends RepresentationModel<M>, D extends DTONode, T extends Node>
        extends BasicGenericRestControllerImpl<M, T> implements GenericRestController<M, D> {

    private final int DEFAULT_DEPTH_FOR_JSON_PATCH = 5;

    private final Class<T> nodeTypeParameterClass;

    private final Class<D> dtoTypeParameterClass;

    protected final GenericService<T, D> service;

    protected final PatchUtil patchUtil;

    protected final ViolationUtil<T, D> violationUtil;

    protected GenericRestControllerImpl(GenericService<T, D> service,
                                        RepresentationModelAssemblerSupport<T, M> modelAssembler,
                                        PagedResourcesAssembler<T> pagedResourcesAssembler,
                                        PatchUtil patchUtil, ViolationUtil<T, D> violationUtil) {

        super(service, modelAssembler, pagedResourcesAssembler);

        this.nodeTypeParameterClass = (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(),
                GenericRestControllerImpl.class)[2];
        this.dtoTypeParameterClass = (Class<D>) GenericTypeResolver.resolveTypeArguments(getClass(),
                GenericRestControllerImpl.class)[1];
        this.service = service;
        this.patchUtil = patchUtil;
        this.violationUtil = violationUtil;
    }

    @PostMapping
    @Override
    public ResponseEntity<M> add(@RequestBody @Valid D dto) {

        T node = service.saveNew(dto);

        M resource = modelAssembler.toModel(node);

        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    @Override
    public ResponseEntity<M> update(@PathVariable("id") Long id, @RequestBody @Valid D dto) {

        Optional<T> nodeOptional = service.findById(id, DEFAULT_DEPTH_FOR_JSON_PATCH);

        if (id != null && nodeOptional.isPresent()) {

            T node = service.update(nodeOptional.get(), dto);

            return new ResponseEntity<>(modelAssembler.toModel(node), HttpStatus.OK);

        } else {

            T node = service.saveNew(dto);

            return new ResponseEntity<>(modelAssembler.toModel(node), HttpStatus.CREATED);
        }
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @Override
    public ResponseEntity<M> updateFields(@PathVariable("id") Long id, @RequestBody JsonPatch objectAsJsonPatch) {

        T node = service.findById(id, DEFAULT_DEPTH_FOR_JSON_PATCH)
                .orElseThrow(() -> new ResourceNotFoundException(modelType, id));

        T nodePatched = patchUtil.patch(objectAsJsonPatch, node, nodeTypeParameterClass);

        violationUtil.violate(nodePatched, dtoTypeParameterClass);

        service.save(nodePatched);

        M resource = modelAssembler.toModel(nodePatched);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @Override
    public ResponseEntity<M> updateFields(@PathVariable("id2") Long id, @RequestBody JsonMergePatch objectAsJsonMergePatch) {

        T node = service.findById(id, DEFAULT_DEPTH_FOR_JSON_PATCH)
                .orElseThrow(() -> new ResourceNotFoundException(modelType, id));

        T nodePatched = patchUtil.mergePatch(objectAsJsonMergePatch, node, nodeTypeParameterClass);

        violationUtil.violate(nodePatched, dtoTypeParameterClass);

        nodePatched = service.save(nodePatched);

        M resource = modelAssembler.toModel(nodePatched);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {

        service.delete(id).orElseThrow(() -> new ResourceNotFoundException(modelType, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
