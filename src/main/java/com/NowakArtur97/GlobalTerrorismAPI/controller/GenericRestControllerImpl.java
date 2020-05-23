package com.NowakArtur97.GlobalTerrorismAPI.controller;


import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
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
public abstract class GenericRestControllerImpl<R extends RepresentationModel<R>, D extends DTONode, T extends Node> implements GenericRestController<R, D> {

    protected final String nodeType;

    protected final Class<T> typeParameterClass;

    protected final Class<D> dtoTypeParameterClass;

    protected final GenericService<T> service;

    protected final RepresentationModelAssemblerSupport<T, R> modelAssembler;

    protected final PagedResourcesAssembler<T> pagedResourcesAssembler;

    protected final PatchHelper patchHelper;

    protected final ViolationHelper violationHelper;

    public GenericRestControllerImpl(GenericService<T> service, RepresentationModelAssemblerSupport<T, R> modelAssembler, PagedResourcesAssembler<T> pagedResourcesAssembler, PatchHelper patchHelper, ViolationHelper violationHelper) {

        this.typeParameterClass = (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(), GenericRestControllerImpl.class)[2];
        this.dtoTypeParameterClass = (Class<D>) GenericTypeResolver.resolveTypeArguments(getClass(), GenericRestControllerImpl.class)[1];
        this.nodeType = this.typeParameterClass.getSimpleName();
        this.service = service;
        this.modelAssembler = modelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.patchHelper = patchHelper;
        this.violationHelper = violationHelper;
    }

    @GetMapping
    @Override
    public ResponseEntity<PagedModel<R>> findAll(Pageable pageable) {

        Page<T> resources = service.findAll(pageable);
        PagedModel<R> pagedModel = pagedResourcesAssembler.toModel(resources, modelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Override
    public ResponseEntity<R> findById(@PathVariable("id") Long id) {

        return service.findById(id).map(modelAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(nodeType, id));
    }

    @PostMapping
    @Override
    public ResponseEntity<R> add(@RequestBody @Valid D dto) {

        T node = service.saveNew(dto);

        R resource = modelAssembler.toModel(node);

        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    @Override
    public ResponseEntity<R> update(@PathVariable("id") Long id, @RequestBody @Valid D dto) {

        HttpStatus httpStatus;
        T node;

        Optional<T> nodeOptional = service.findById(id);

        if (id != null && nodeOptional.isPresent()) {

            httpStatus = HttpStatus.OK;

            node = service.update(nodeOptional.get(), dto);

        } else {

            httpStatus = HttpStatus.CREATED;

            node = service.saveNew(dto);
        }

        R resource = modelAssembler.toModel(node);

        return new ResponseEntity<>(resource, httpStatus);
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @Override
    public ResponseEntity<R> updateFields(@PathVariable("id") Long id, @RequestBody JsonPatch objectAsJsonPatch) {

        T node = service.findById(id).orElseThrow(() -> new ResourceNotFoundException(nodeType, id));

        T nodePatched = patchHelper.patch(objectAsJsonPatch, node, typeParameterClass);

        violationHelper.violate(nodePatched, dtoTypeParameterClass);

        nodePatched = service.save(nodePatched);

        R resource = modelAssembler.toModel(nodePatched);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @Override
    public ResponseEntity<R> updateFields(@PathVariable("id2") Long id, @RequestBody JsonMergePatch objectAsJsonMergePatch) {

        T node = service.findById(id).orElseThrow(() -> new ResourceNotFoundException(nodeType, id));

        T nodePatched = patchHelper.mergePatch(objectAsJsonMergePatch, node, typeParameterClass);

        violationHelper.violate(nodePatched, dtoTypeParameterClass);

        nodePatched = service.save(nodePatched);

        R resource = modelAssembler.toModel(nodePatched);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {

        service.delete(id).orElseThrow(() -> new ResourceNotFoundException(nodeType, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
