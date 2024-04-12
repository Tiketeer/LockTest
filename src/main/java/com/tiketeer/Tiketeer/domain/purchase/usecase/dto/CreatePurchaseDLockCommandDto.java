package com.tiketeer.Tiketeer.domain.purchase.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePurchaseDLockCommandDto {
	private final String memberEmail;
	private final UUID ticketingId;
	private final Integer count;
	private final Long waitTime;
	private final Long leaseTime;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public CreatePurchaseDLockCommandDto(
		String memberEmail, UUID ticketingId, Integer count, Long waitTime, Long leaseTime,
		LocalDateTime commandCreatedAt) {
		this.memberEmail = memberEmail;
		this.ticketingId = ticketingId;
		this.count = count;
		this.waitTime = waitTime;
		this.leaseTime = leaseTime;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
