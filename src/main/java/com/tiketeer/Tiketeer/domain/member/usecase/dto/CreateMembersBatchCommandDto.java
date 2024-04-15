package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateMembersBatchCommandDto {
	private final List<String> emailList;

	@Builder
	public CreateMembersBatchCommandDto(List<String> emailList) { this.emailList = emailList; }
}
