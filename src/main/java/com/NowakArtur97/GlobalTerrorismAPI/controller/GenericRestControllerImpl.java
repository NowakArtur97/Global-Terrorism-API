package com.NowakArtur97.GlobalTerrorismAPI.controller;

import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GenericRestControllerImpl<M extends RepresentationModelAssemblerSupport, T, D> implements GenericRestController<M, D> {

    private final GenericService<T> service;

//    private final RepresentationModelAssemblerSupport<T, M extends RepresentationModel<?>> modelAssembler;

    private final PagedResourcesAssembler<M> pagedResourcesAssembler;

    private final PatchHelper patchHelper;

    private final ViolationHelper violationHelper;

    @Override
    public ResponseEntity<PagedModel<M>> findAll(Pageable pageable) {

        return null;
    }

    @Override
    public ResponseEntity<M> findById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<M> add(D dto) {
        return null;
    }

    @Override
    public ResponseEntity<M> update(Long id, @Valid D dto) {
        return null;
    }

    @Override
    public ResponseEntity<M> updateFields(Long id, JsonPatch objectAsJsonPatch) {
        return null;
    }

    @Override
    public ResponseEntity<M> updateFields(Long id, JsonMergePatch objectAsJsonMergePatch) {
        return null;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return null;
    }
}
