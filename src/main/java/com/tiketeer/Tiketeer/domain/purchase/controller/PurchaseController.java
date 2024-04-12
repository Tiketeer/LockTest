package com.tiketeer.Tiketeer.domain.purchase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.purchase.controller.dto.PostPurchaseDLockRequestDto;
import com.tiketeer.Tiketeer.domain.purchase.controller.dto.PostPurchasePLockRequestDto;
import com.tiketeer.Tiketeer.domain.purchase.controller.dto.PostPurchaseResponseDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.CreatePurchaseDLockUseCase;
import com.tiketeer.Tiketeer.domain.purchase.usecase.CreatePurchasePLockUseCase;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

	private final CreatePurchasePLockUseCase createPurchasePLockUseCase;
	private final CreatePurchaseDLockUseCase createPurchaseDLockUseCase;

	@Autowired
	PurchaseController(CreatePurchasePLockUseCase createPurchasePLockUseCase,
		CreatePurchaseDLockUseCase createPurchaseDLockUseCase) {
		this.createPurchasePLockUseCase = createPurchasePLockUseCase;
		this.createPurchaseDLockUseCase = createPurchaseDLockUseCase;
	}

	@PostMapping("/p-lock")
	public ResponseEntity<ApiResponse<PostPurchaseResponseDto>> postPurchaseWithPLock(
		@Valid @RequestBody PostPurchasePLockRequestDto request) {
		var result = createPurchasePLockUseCase.createPurchase(request.convertToDto());
		var responseBody = ApiResponse.wrap(PostPurchaseResponseDto.converFromDto(result));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}

	@PostMapping("/d-lock")
	public ResponseEntity<ApiResponse<PostPurchaseResponseDto>> postPurchaseWithDLock(
		@Valid @RequestBody PostPurchaseDLockRequestDto request) {
		var result = createPurchaseDLockUseCase.createPurchase(request.convertToDto());
		var responseBody = ApiResponse.wrap(PostPurchaseResponseDto.converFromDto(result));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}
}