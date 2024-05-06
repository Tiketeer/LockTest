package com.tiketeer.Tiketeer.domain.purchase.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePurchaseOLockCommandDto extends CreatePurchaseCommandDto {
	private final Long maxBackoff;
	private final Long minBackoff;
	private final Integer maxAttempts;

	@Builder
	public CreatePurchaseOLockCommandDto(
		String memberEmail, UUID ticketingId, Integer count, Long maxBackoff, Long minBackoff, Integer maxAttempts,
		LocalDateTime commandCreatedAt) {
		super(memberEmail, ticketingId, count, commandCreatedAt);
		this.maxBackoff = maxBackoff;
		this.minBackoff = minBackoff;
		this.maxAttempts = maxAttempts;
	}
}
