package com.nowakArtur97.globalTerrorismAPI.common.controller;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import com.nowakArtur97.globalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericService;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public abstract class BasicGenericRestControllerImpl<M extends RepresentationModel<M>, T extends Node>
        implements BasicGenericRestController<M> {

    protected final String modelType;

    protected final Class<M> modelTypeParameterClass;

    protected final BasicGenericService<T> service;

    protected final RepresentationModelAssemblerSupport<T, M> modelAssembler;

    protected final PagedResourcesAssembler<T> pagedResourcesAssembler;

    protected BasicGenericRestControllerImpl(BasicGenericService<T> service,
                                             RepresentationModelAssemblerSupport<T, M> modelAssembler,
                                             PagedResourcesAssembler<T> pagedResourcesAssembler) {

        this.modelTypeParameterClass = (Class<M>) GenericTypeResolver.resolveTypeArguments(getClass(),
                BasicGenericRestControllerImpl.class)[0];
        this.modelType = this.modelTypeParameterClass.getSimpleName();
        this.service = service;
        this.modelAssembler = modelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    @Override
    public ResponseEntity<PagedModel<M>> findAll(Pageable pageable) {

        Page<T> resources = service.findAll(pageable);
        PagedModel<M> pagedModel = pagedResourcesAssembler.toModel(resources, modelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Override
    public ResponseEntity<M> findById(@PathVariable("id") Long id) {

        return service.findById(id).map(modelAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(modelType, id));
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Override
    public ResponseEntity<?> collectionOptions() {

        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.OPTIONS)
                .build();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    @Override
    public ResponseEntity<?> singularOptions() {

        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.OPTIONS)
                .build();
    }
}
