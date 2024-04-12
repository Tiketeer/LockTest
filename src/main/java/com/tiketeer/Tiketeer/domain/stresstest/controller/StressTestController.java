package com.tiketeer.Tiketeer.domain.stresstest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.stresstest.usecase.CleanUpUseCase;

@RestController
@RequestMapping("/stress-test")
public class StressTestController {
	private CleanUpUseCase cleanUpUseCase;

	@Autowired
	public StressTestController(CleanUpUseCase cleanUpUseCase) {
		this.cleanUpUseCase = cleanUpUseCase;
	}

	@DeleteMapping
	public void cleanUp() {
		cleanUpUseCase.cleanUp();
	}
}
