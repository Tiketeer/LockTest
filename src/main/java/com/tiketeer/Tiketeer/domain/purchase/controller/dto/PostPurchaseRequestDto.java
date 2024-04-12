package com.tiketeer.Tiketeer.domain.purchase.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseCommandDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class PostPurchaseRequestDto {
	@NotNull
	private final UUID ticketingId;

	@NotNull
	private final Integer count;
	@NotBlank
	private final String email;

	@Builder
	public PostPurchaseRequestDto(@NotNull UUID ticketingId,
		@NotNull Integer count, @NotBlank String email) {
		this.ticketingId = ticketingId;
		this.count = count;
		this.email = email;
	}

	public CreatePurchaseCommandDto convertToDto() {
		return CreatePurchaseCommandDto.builder()
			.memberEmail(email)
			.ticketingId(this.ticketingId)
			.count(this.count)
			.build();
	}
}
