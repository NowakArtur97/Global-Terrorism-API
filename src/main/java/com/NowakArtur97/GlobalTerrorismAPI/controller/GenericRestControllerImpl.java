package com.NowakArtur97.GlobalTerrorismAPI.controller;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;
import java.util.Optional;

@RestController
public abstract class GenericRestControllerImpl<R extends RepresentationModel<R>, D extends DTONode, T extends Node> implements GenericRestController<R, D> {

    protected final String nodeType;

    protected final Class<T> typeParameterClass;

    protected final GenericService<T> service;

    protected final RepresentationModelAssembler<T, R> modelAssembler;

    protected final PagedResourcesAssembler<R> pagedResourcesAssembler;

    protected final PatchHelper patchHelper;

    protected final ViolationHelper violationHelper;

    public GenericRestControllerImpl(GenericService<T> service, RepresentationModelAssembler<T, R> modelAssembler, PagedResourcesAssembler<R> pagedResourcesAssembler, PatchHelper patchHelper, ViolationHelper violationHelper) {
        this.typeParameterClass = (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(), GenericRestControllerImpl.class)[0];
        this.nodeType = this.typeParameterClass.getSimpleName();
        this.service = service;
        this.modelAssembler = modelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.patchHelper = patchHelper;
        this.violationHelper = violationHelper;
    }

    @Override
    public ResponseEntity<PagedModel<R>> findAll(Pageable pageable) {

        return null;
    }

    @Override
    public ResponseEntity<R> findById(@PathVariable("id") Long id) {

        return service.findById(id).map(modelAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(nodeType, id));
    }

    @Override
    public ResponseEntity<R> add(D dto) {

        T node = service.saveNew(dto);

        R resource = modelAssembler.toModel(node);

        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<R> update(Long id, @Valid D dto) {

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

    @Override
    public ResponseEntity<R> updateFields(Long id, JsonPatch objectAsJsonPatch) {

        T node = service.findById(id).orElseThrow(() -> new ResourceNotFoundException(nodeType, id));

        T nodePatched = patchHelper.patch(objectAsJsonPatch, node, typeParameterClass);

        violationHelper.violate(nodePatched, DTONode.class);

        nodePatched = service.save(nodePatched);

        R resource = modelAssembler.toModel(nodePatched);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<R> updateFields(Long id, JsonMergePatch objectAsJsonMergePatch) {

        T node = service.findById(id).orElseThrow(() -> new ResourceNotFoundException(nodeType, id));

        T nodePatched = patchHelper.mergePatch(objectAsJsonMergePatch, node, typeParameterClass);

        violationHelper.violate(nodePatched, DTONode.class);

        nodePatched = service.save(nodePatched);

        R resource = modelAssembler.toModel(nodePatched);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {

        service.delete(id).orElseThrow(() -> new ResourceNotFoundException(nodeType, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
