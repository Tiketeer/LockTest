package com.tiketeer.Tiketeer.domain.purchase.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePurchaseOLockCommandDto {
	private final String memberEmail;
	private final UUID ticketingId;
	private final Integer count;
	private final Long backoff;
	private final Integer maxAttempts;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public CreatePurchaseOLockCommandDto(
		String memberEmail, UUID ticketingId, Integer count, Long backoff, Integer maxAttempts,
		LocalDateTime commandCreatedAt) {
		this.memberEmail = memberEmail;
		this.ticketingId = ticketingId;
		this.count = count;
		this.backoff = backoff;
		this.maxAttempts = maxAttempts;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
