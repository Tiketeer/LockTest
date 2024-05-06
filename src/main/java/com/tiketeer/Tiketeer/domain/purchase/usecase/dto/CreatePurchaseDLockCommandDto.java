package com.tiketeer.Tiketeer.domain.purchase.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePurchaseDLockCommandDto extends CreatePurchaseCommandDto {
	private final Long waitTime;
	private final Long leaseTime;

	@Builder
	public CreatePurchaseDLockCommandDto(
		String memberEmail, UUID ticketingId, Integer count, Long waitTime, Long leaseTime,
		LocalDateTime commandCreatedAt) {
		super(memberEmail, ticketingId, count, commandCreatedAt);
		this.waitTime = waitTime;
		this.leaseTime = leaseTime;
	}
}
