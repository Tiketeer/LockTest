package com.tiketeer.Tiketeer.domain.purchase.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseDLockCommandDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class PostPurchaseDLockRequestDto {
	@NotNull
	private final UUID ticketingId;

	@NotNull
	private final Integer count;
	@NotBlank
	private final String email;
	@NotNull
	private final Long waitTime;
	@NotNull
	private final Long leaseTime;

	@Builder
	public PostPurchaseDLockRequestDto(@NotNull UUID ticketingId,
		@NotNull Integer count, @NotBlank String email, @NotNull Long waitTime, @NotNull Long leaseTime) {
		this.ticketingId = ticketingId;
		this.count = count;
		this.email = email;
		this.waitTime = waitTime;
		this.leaseTime = leaseTime;
	}

	public CreatePurchaseDLockCommandDto convertToDto() {
		return CreatePurchaseDLockCommandDto.builder()
			.memberEmail(email)
			.ticketingId(ticketingId)
			.count(count)
			.waitTime(waitTime)
			.leaseTime(leaseTime)
			.build();
	}
}
