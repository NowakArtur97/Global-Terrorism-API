package com.NowakArtur97.GlobalTerrorismAPI.controller;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;

public interface GenericRestController<M extends RepresentationModel<M>, D extends DTONode> {

    @GetMapping
    ResponseEntity<PagedModel<M>> findAll(Pageable pageable);

    @GetMapping(path = "/{id}")
    ResponseEntity<M> findById(Long id);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
        // Added to remove the default 200 status added by Swagger
    ResponseEntity<M> add(D dto);

    @PutMapping(path = "/{id}")
    ResponseEntity<M> update(@PathVariable("id") Long id, @RequestBody @Valid D dto);

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    ResponseEntity<M> updateFields(@PathVariable("id") Long id, @RequestBody JsonPatch objectAsJsonPatch);

    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    ResponseEntity<M> updateFields(@PathVariable("id2") Long id, @RequestBody JsonMergePatch objectAsJsonMergePatch);

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
        // Added to remove the default 200 status added by Swagger
    ResponseEntity<Void> delete(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> collectionOptions();

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    ResponseEntity<?> singularOptions();
}
