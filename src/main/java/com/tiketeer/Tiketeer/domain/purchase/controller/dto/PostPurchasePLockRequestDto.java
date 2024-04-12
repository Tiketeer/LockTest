package com.tiketeer.Tiketeer.domain.purchase.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchasePLockCommandDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class PostPurchasePLockRequestDto {
	@NotNull
	private final UUID ticketingId;

	@NotNull
	private final Integer count;
	@NotBlank
	private final String email;

	@Builder
	public PostPurchasePLockRequestDto(@NotNull UUID ticketingId,
		@NotNull Integer count, @NotBlank String email) {
		this.ticketingId = ticketingId;
		this.count = count;
		this.email = email;
	}

	public CreatePurchasePLockCommandDto convertToDto() {
		return CreatePurchasePLockCommandDto.builder()
			.memberEmail(email)
			.ticketingId(ticketingId)
			.count(count)
			.build();
	}
}
