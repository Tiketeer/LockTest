package com.tiketeer.Tiketeer.domain.member.controller.dto;

import java.util.List;

import org.hibernate.annotations.ListIndexJavaType;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class CreateMembersRequestDto {
	@NotEmpty
	private final List<String> emailList;

	@Builder
	public CreateMembersRequestDto(List<String> emailList) { this.emailList = emailList; }
}
