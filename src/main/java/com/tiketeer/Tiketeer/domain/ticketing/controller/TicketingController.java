package com.tiketeer.Tiketeer.domain.ticketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingRequestDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.CreateTicketingUseCase;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ticketings")
public class TicketingController {
	private final CreateTicketingUseCase createTicketingUseCase;

	@Autowired
	public TicketingController(CreateTicketingUseCase createTicketingUseCase) {
		this.createTicketingUseCase = createTicketingUseCase;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<PostTicketingResponseDto>> postTicketing(
		@Valid @RequestBody PostTicketingRequestDto request) {
		var result = createTicketingUseCase.createTicketing(request.convertToDto());
		var responseBody = ApiResponse.wrap(PostTicketingResponseDto.convertFromDto(result));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}
}
