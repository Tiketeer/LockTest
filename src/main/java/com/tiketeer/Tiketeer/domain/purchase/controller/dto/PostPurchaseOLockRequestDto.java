package com.tiketeer.Tiketeer.domain.purchase.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseOLockCommandDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class PostPurchaseOLockRequestDto {
	@NotNull
	private final UUID ticketingId;

	@NotNull
	private final Integer count;
	@NotBlank
	private final String email;
	@NotNull
	private final Long backoff;
	@NotNull
	private final Integer maxAttempts;

	@Builder
	public PostPurchaseOLockRequestDto(@NotNull UUID ticketingId,
		@NotNull Integer count, @NotBlank String email, @NotNull Long backoff, @NotNull Integer maxAttempts) {
		this.ticketingId = ticketingId;
		this.count = count;
		this.email = email;
		this.backoff = backoff;
		this.maxAttempts = maxAttempts;
	}

	public CreatePurchaseOLockCommandDto convertToDto() {
		return CreatePurchaseOLockCommandDto.builder()
			.memberEmail(email)
			.ticketingId(ticketingId)
			.count(count)
			.backoff(backoff)
			.maxAttempts(maxAttempts)
			.build();
	}
}
