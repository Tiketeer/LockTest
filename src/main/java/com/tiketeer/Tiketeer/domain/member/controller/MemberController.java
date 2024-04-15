package com.tiketeer.Tiketeer.domain.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.member.controller.dto.CreateMembersRequestDto;
import com.tiketeer.Tiketeer.domain.member.usecase.CreateMembersBatchUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.CreateMembersBatchCommandDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/members")
public class MemberController {
	private final CreateMembersBatchUseCase createMembersBatchUseCase;

	@Autowired
	public MemberController(CreateMembersBatchUseCase createMembersBatchUseCase) {
		this.createMembersBatchUseCase = createMembersBatchUseCase;
	}

	@PostMapping
	public ResponseEntity createMembers(@Valid @RequestBody CreateMembersRequestDto request) {
		createMembersBatchUseCase.createMembersInBatch(
			CreateMembersBatchCommandDto.builder()
				.emailList(request.getEmailList())
				.build()
		);
		return ResponseEntity.ok().build();
	}
}
