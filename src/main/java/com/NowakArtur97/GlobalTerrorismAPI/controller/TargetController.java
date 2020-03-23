package com.NowakArtur97.GlobalTerrorismAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NowakArtur97.GlobalTerrorismAPI.node.Target;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/targets")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TargetController {

	private final TargetService targetService;

	@GetMapping(path = "/")
	public Iterable<Target> findAllTargets() {

		return targetService.findAll();
	}
}
