package com.NowakArtur97.GlobalTerrorismAPI.controller;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface BasicGenericRestController<M extends RepresentationModel<M>, D extends DTONode> {

    @GetMapping
    ResponseEntity<PagedModel<M>> findAll(Pageable pageable);

    @GetMapping(path = "/{id}")
    ResponseEntity<M> findById(Long id);

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> collectionOptions();

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    ResponseEntity<?> singularOptions();
}
