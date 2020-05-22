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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;

@RestController
public abstract class GenericRestControllerImpl<R extends RepresentationModel<R>, T extends Node> implements GenericRestController<R> {

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
    public ResponseEntity<R> findById(Long id) {

        return service.findById(id).map(modelAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(nodeType, id));
    }

    @Override
    public ResponseEntity<R> add(DTONode dto) {
        return null;
    }

    @Override
    public ResponseEntity<R> update(Long id, @Valid DTONode dto) {
        return null;
    }

    @Override
    public ResponseEntity<R> updateFields(Long id, JsonPatch objectAsJsonPatch) {
        return null;
    }

    @Override
    public ResponseEntity<R> updateFields(Long id, JsonMergePatch objectAsJsonMergePatch) {
        return null;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return null;
    }
}
