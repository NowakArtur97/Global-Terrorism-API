package com.nowakArtur97.globalTerrorismAPI.common.controller;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.DTO;
import com.nowakArtur97.globalTerrorismAPI.common.mediaType.PatchMediaType;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;

public interface GenericRestController<M extends RepresentationModel<M>, D extends DTO> extends BasicGenericRestController<M> {

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
}
